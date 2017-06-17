package com.focus3d.game.handler.logic.card;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.protocal.GameMessage;

/**
 * 打牌逻辑
 * *
 * @author lihaijun
 *
 */
public class PlayCardLogic {
	/**
	 * 
	 * *
	 * @param ctx
	 * @param message
	 */
	public static void play(ChannelHandlerContext ctx, GameMessage message){
		//打牌
		Group group = GroupDB.select(ctx.channel());
		List<User> userList = group.getUserList();

		String body = String.valueOf(message.getBody());
		if(!StringUtil.isNullOrEmpty(body)){
			JSONObject bodyJo = JSONObject.fromObject(body);
			String sendCardUserId = bodyJo.getString("userid");
			String sendCard = bodyJo.getString("card");
			int control = bodyJo.getInt("control");
			if(!StringUtil.isNullOrEmpty(sendCardUserId)){
				System.out.println("玩家:" + sendCardUserId + "发牌：" + sendCard);
				//当前玩家
				User currentUser = RobHostLogic.getUser(sendCardUserId, userList);
				currentUser.getCard().setSend(!StringUtil.isNullOrEmpty(sendCard));
				//找出上家出牌的玩家
				User prevSendCardUser = findPrevSendCard(currentUser.getId(), userList);
				System.out.println("当前玩家：" + currentUser + " 的上家：" + prevSendCardUser + " 出了牌");
				for (User user : userList) {
					//计算剩余牌
					if(user.getId().equals(sendCardUserId)){
						Integer remainCard = user.getCard().getRemainCard();
						if(remainCard > 0){
							user.getCard().setRemainCard(remainCard - (StringUtil.isNullOrEmpty(sendCard) ? 0 : JSONArray.fromObject(sendCard).size()));
							break;
						}
					}
				}
				for (User user : userList) {
					System.out.println("玩家:" + user.getId() + ",收到玩家：" + sendCardUserId + "的牌：" + sendCard);
					user.getChannel().writeAndFlush(buildCardSendResp(sendCardUserId, currentUser.getCard().getRemainCard(), sendCard, control, prevSendCardUser.getId()));
				}
				//给下家发送出牌消息
				User nextUser = RobHostLogic.nextUser(sendCardUserId, userList);
				System.out.println("轮到下家:" + nextUser + " 出牌");
				nextUser.getChannel().writeAndFlush(buildNexUserSendCardResp());
			}
		}
		boolean gameOver = false;
		for (User user : userList) {
			if(user.getCard().getRemainCard() == 0){
				System.out.println("玩家：" + user.getId() + " 已经打完手上牌，游戏结束");
				gameOver = true;
				break;
			}
		}
		if(gameOver){
			for (User user : userList) {
				user.getChannel().writeAndFlush(buildGameOverResp());
			}
		} 
	}
	
	/**
	 * 打牌
	 * *
	 * @param sendUserId 出牌者id
	 * @param card 出牌者出的牌
	 * @return
	 */
	private static GameMessage buildCardSendResp(String userId, int remain, String card, int control, String prevSendCardUserId) {
		JSONObject jo = new JSONObject();
		jo.put("userid", userId);
		jo.put("card", card);
		jo.put("control", String.valueOf(control));
		jo.put("remain", String.valueOf(remain));
		jo.put("prevuserid", prevSendCardUserId);
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.CARD_SEND_RESP.getType());
		message.setBody(jo + "\0");
		return message;
	}
	/**
	 * 
	 * *
	 * @return
	 */
	private static GameMessage buildNexUserSendCardResp() {
		JSONObject jo = new JSONObject();
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.CARD_NEXT_SEND_RESP.getType());
		message.setBody(jo + "\0");
		return message;
	}
	/**
	 * 
	 * *
	 * @return
	 */
	private static GameMessage buildGameOverResp() {
		JSONObject jo = new JSONObject();
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.CARD_SEND_OVER.getType());
		message.setBody(jo + "\0");
		return message;
	}
	
	/**
	 * 找出前一个出牌的玩家
	 * *
	 * @param currentUserId
	 * @param userList
	 * @return
	 */
	private static User findPrevSendCard(String currentUserId, List<User> userList){
		User prevUser = RobHostLogic.prevUser(currentUserId, userList);
		String robHostUserId = "";
		boolean isSend = prevUser.getCard().isSend();
		if(isSend){
			robHostUserId = prevUser.getId();
		}
		if(StringUtil.isNullOrEmpty(robHostUserId)){
			prevUser = findPrevSendCard(prevUser.getId(), userList);
		}
		return prevUser;
	}
	
}
