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
		String body = String.valueOf(message.getBody());
		if(!StringUtil.isNullOrEmpty(body)){
			JSONObject bodyJo = JSONObject.fromObject(body);
			String sendCardUserId = bodyJo.getString("userid");
			String sendCard = bodyJo.getString("card");
			if(!StringUtil.isNullOrEmpty(sendCardUserId) && !StringUtil.isNullOrEmpty(sendCard)){
				System.out.println("玩家:" + sendCardUserId + "发牌：" + sendCard);
				Group group = GroupDB.select(ctx.channel());
				List<User> userList = group.getUserList();
				for (User user : userList) {
					//计算剩余牌
					if(user.getId().equals(sendCardUserId)){
						Integer remainCard = user.getCard().getRemainCard();
						if(remainCard > 0){
							user.getCard().setRemainCard(remainCard - JSONArray.fromObject(sendCard).size());
						}
					}
					System.out.println("玩家:" + user.getId() + ",收到玩家：" + sendCardUserId + "的牌：" + sendCard);
					user.getChannel().writeAndFlush(buildCardSendResp(sendCardUserId, user, sendCard));
				}
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
	private static GameMessage buildCardSendResp(String userId, User user, String card) {
		JSONObject jo = new JSONObject();
		jo.put("userid", userId);
		jo.put("card", card);
		jo.put("remain", user.getCard().getRemainCard());
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.CARD_SEND_RESP.getType());
		message.setBody(jo + "\0");
		return message;
	}
}