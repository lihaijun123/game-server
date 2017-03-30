package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
			if(message.getHeader().getType() == MessageType.BUSINESS_REQ.getType()){
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
		
	    
}
