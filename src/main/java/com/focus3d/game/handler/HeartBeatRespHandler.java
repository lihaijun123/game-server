package com.focus3d.game.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;


public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
	private int heartbeatCount = 0;
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GameMessage message = (GameMessage)msg;
		if(message.getHeader().getType() == MessageType.HEARTBEAT_REQ.getType()){
			System.out.println("server->get ping msg from " + ctx.channel().remoteAddress() + "-> " + msg.toString());
			sendPongMsg(ctx);
		}
	}

	
	private void sendPongMsg(ChannelHandlerContext ctx) {
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.HEARTBEAT_RESP.getType());
		System.out.println("server->sent pong msg to " + ctx.channel().remoteAddress() + ", count: " + heartbeatCount);
        heartbeatCount ++;
        ctx.writeAndFlush(message);
    }
	
}
