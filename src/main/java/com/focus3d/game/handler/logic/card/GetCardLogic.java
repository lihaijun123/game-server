package com.focus3d.game.handler.logic.card;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.focus3d.game.card.Card;
import com.focus3d.game.card.CardManager;
import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.protocal.GameMessage;

/**
 * 
 * *
 * @author lihaijun
 *
 */
public class GetCardLogic {
	//游戏玩家个数
	public static final int PLAYER_NUM = 2;
	/**
	 * 发牌
	 * *
	 * @param ctx
	 * @param message
	 */
	public static void getCard(ChannelHandlerContext ctx, GameMessage message){
		//发牌
		Group group = GroupDB.select(ctx.channel());
		List<User> userList = group.getUserList();
		String body = String.valueOf(message.getBody());
		if(!StringUtil.isNullOrEmpty(body)){
			JSONObject bodyJo = JSONObject.fromObject(body);
			String currentUserId = bodyJo.getString("userid");
			User currentUser = RobHostLogic.getUser(currentUserId, userList);
			currentUser.getCard().setReady(true);
			ctx.writeAndFlush(buildCardGetResp(MessageType.CARD_GET_RESP, null, null, null));
		}
		boolean isAllReady = true;
		for (User user : userList) {
			Card card = user.getCard();
			if(!card.isReady()){
				isAllReady = false;
				break;
			}
		}
		if(userList.size() == PLAYER_NUM && isAllReady){
			CardManager cardManager = new CardManager();
			Map<String, Card> shuffleCards = cardManager.ShuffleCards();
			Card bootomCard = shuffleCards.get(CardManager.PLAYER_KEY_BOTTOM_CARD);
			for(int i = 0; i < userList.size(); i ++){
				User user = userList.get(i);
				Card card = shuffleCards.get(CardManager.PLAYER_KEY_PREFIX + (i + 1));
				user.setCard(card);
				card.setBootomCard(bootomCard);
				//手上牌张数
				card.setRemainCard(card.getData().split(",").length);
			}
			//发牌
			for(User user : userList){
				GameMessage cardGetResp = buildCardGetResp(MessageType.CARD_PUSH_RESP, user, userList, group);
				user.getChannel().writeAndFlush(cardGetResp);
			}
			//随机选出一个叫地主玩家
			User callHostUser = userList.get((new Random()).nextInt(userList.size()));
			callHostUser.getCard().setCaller(true);
			System.out.println("玩家：" + callHostUser.toString() + " 被选出叫地主权利。");
			GameMessage cardGetResp = buildCallHostResp(callHostUser);
			callHostUser.getChannel().writeAndFlush(cardGetResp);
		} else {
			System.out.println("组[" + group.getId() + "]成员数：" + userList.size() + " 小于系统设定的玩家个数 " + PLAYER_NUM + "，不可以发牌");
		}
	}
	
	/**
	 * 发牌
	 * *
	 * @param jo
	 * @return
	 */
	private static GameMessage buildCardGetResp(MessageType messageType, User currentUser , List<User> userList, Group group) {
		JSONObject jo = new JSONObject();
		if(currentUser != null){
			Card card = currentUser.getCard();
			Card bootomCard = currentUser.getCard().getBootomCard();
			jo.put("groupid", group.getId());
			jo.put("userid", currentUser.getId());
			jo.put("card", card.getData());
			jo.put("dp", bootomCard.toString());
			JSONArray jsonArray = new JSONArray();
			for(User user : userList){
				if(!user.equals(currentUser)){
					JSONObject otherJo = new JSONObject();
					otherJo.put("userid", user.getId());
					otherJo.put("card", user.getCard().getData());
					jsonArray.add(otherJo);
				}
			}
			jo.put("other", jsonArray.toString());
		}
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)messageType.getType());
		message.setBody(jo + "\0");
		return message;
	}
	/**
	 * 随机分配给玩家叫地主
	 * *
	 * @param user
	 * @return
	 */
	public static GameMessage buildCallHostResp(User user) {
		JSONObject jo = new JSONObject();
		jo.put("userid", user.getId());
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.USER_ROB_HOST_CALL_RESP.getType());
		message.setBody(jo + "\0");
		return message;
	}
}
