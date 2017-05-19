package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;

import com.focus3d.game.card.Card;
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
			System.out.println(this.toString());
			GameMessage message = (GameMessage)msg;
			if(message.getHeader().getType() == MessageType.CARD_GET_REQ.getType()){
				Group group = GroupDB.select(ctx.channel());
				List<User> userList = group.getUserList();
				if(userList.size() > 0){
					//发牌请求
					String body = String.valueOf(message.getBody());
					if(!StringUtil.isNullOrEmpty(body)){
						JSONObject bodyJo = JSONObject.fromObject(body);
						
					}
					Card card = new Card();
					Map<String, String> shuffleCards = card.ShuffleCards();
					String dp = shuffleCards.get("dp");
					
					for(int i = 0; i <userList.size(); i ++){
						User user = userList.get(i);
						JSONObject jo = new JSONObject();
						jo.put("card", shuffleCards.get("ply_" + (i + 1)));
						jo.put("dp", dp);
						GameMessage cardGetResp = buildCardGetResp(jo);
						user.getChannel().writeAndFlush(cardGetResp);
					}
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
		
		private GameMessage buildCardGetResp(JSONObject jo) {
			GameMessage message = new GameMessage();
			message.getHeader().setType((byte)MessageType.CARD_GET_RESP.getType());
			message.setBody(jo);
			return message;
		}
	    
}
