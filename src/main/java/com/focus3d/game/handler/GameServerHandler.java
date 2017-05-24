package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.focus3d.game.card.Card;
import com.focus3d.game.card.CardManager;
import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;

public class GameServerHandler extends ChannelInboundHandlerAdapter {
	public static Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	@Override
	    public void channelActive(final ChannelHandlerContext ctx) { // (1)
			System.out.println(channels.toString());
			String id = String.valueOf(ctx.channel().id());
			if(!channels.containsKey(id)){
				channels.put(id, ctx.channel());
				System.out.println("now have " + channels.size() + " join in");
			}
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        cause.printStackTrace();
	        ctx.close();
	    }

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			GameMessage message = (GameMessage)msg;
			if(message.getHeader().getType() == MessageType.CARD_GET_REQ.getType()){
				Group group = GroupDB.select(ctx.channel());
				List<User> userList = group.getUserList();
				if(userList.size() == 3){
					//发牌请求
					String body = String.valueOf(message.getBody());
					if(!StringUtil.isNullOrEmpty(body)){
						JSONObject bodyJo = JSONObject.fromObject(body);
						
					}
					CardManager cardManager = new CardManager();
					Map<String, Card> shuffleCards = cardManager.ShuffleCards();
					Card bootomCard = shuffleCards.get(CardManager.PLAYER_KEY_BOTTOM_CARD);
					for(int i = 0; i < userList.size(); i ++){
						User user = userList.get(i);
						Card card = shuffleCards.get(CardManager.PLAYER_KEY_PREFIX + (i + 1));
						user.setCard(card);
						user.setBootomCard(bootomCard);
					}
					for(User user : userList){
						GameMessage cardGetResp = buildCardGetResp(user, userList, group);
						user.getChannel().writeAndFlush(cardGetResp);
					}
				} else {
					ctx.writeAndFlush(buildCardGetResp(null, null, null));
				}
			} else if(message.getHeader().getType() == MessageType.BUSINESS_REQ.getType()){
				System.out.println("客户端请求消息->" + msg);
				//业务处理
			} else {
				ctx.fireChannelRead(msg);
			}
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			String id = String.valueOf(ctx.channel().id());
			if(channels.containsKey(id)){
				channels.remove(id);
			}
		}
		/**
		 * 发牌请求响应
		 * *
		 * @param jo
		 * @return
		 */
		private GameMessage buildCardGetResp(User currentUser , List<User> userList, Group group) {
			JSONObject jo = new JSONObject();
			if(currentUser != null){
				Card card = currentUser.getCard();
				Card bootomCard = currentUser.getBootomCard();
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
			message.getHeader().setType((byte)MessageType.CARD_GET_RESP.getType());
			message.setBody(jo);
			return message;
		}
	    
}
