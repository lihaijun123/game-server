package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focus3d.game.constant.MessageType;
import com.focus3d.game.handler.logic.card.GetCardLogic;
import com.focus3d.game.handler.logic.card.PlayCardLogic;
import com.focus3d.game.handler.logic.card.RobHostLogic;
import com.focus3d.game.protocal.GameMessage;
/**
 * 
 * *
 * @author lihaijun
 *
 */
public class GameServerHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(GameServerHandler.class);
	
	public static Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	
	@Override
	    public void channelActive(final ChannelHandlerContext ctx) { // (1)
			System.out.println(channels.toString());
			String id = String.valueOf(ctx.channel().id());
			if(!channels.containsKey(id)){
				channels.put(id, ctx.channel());
				System.out.println("已有 " + channels.size() + " 客户端连接");
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
				//发牌
				GetCardLogic.getCard(ctx, message);
			} else if(message.getHeader().getType() == MessageType.CARD_SEND_REQ.getType()) {
				//打牌
				PlayCardLogic.play(ctx, message);
			} else if(message.getHeader().getType() == MessageType.USER_ROB_HOST_REQ.getType()){
				//叫地主
				RobHostLogic.rotHost(ctx, message);
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
		
}
