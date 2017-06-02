package com.focus3d.game.handler;


import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import com.focus3d.game.client.GameClient;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.protocal.GameMessage;


public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
	private int heartbeatCount = 0;
	private GameClient client;
	private volatile ScheduledFuture<?> heartBeat;
	public HeartBeatReqHandler(GameClient gameClient) {
		this.client = gameClient;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GameMessage message = (GameMessage)msg;
		if(message.getHeader().getType() == MessageType.LOGIN_RESP.getType()){
			System.out.println("client->start heartbeat task");
			heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 2000, TimeUnit.MILLISECONDS);
		} else if(message.getHeader().getType() == MessageType.HEARTBEAT_RESP.getType()){
			System.out.println("client->get pong msg from " + ctx.channel().remoteAddress() + "-> " + msg.toString());
		}
	}
	
	class HeartBeatTask implements Runnable {
		private ChannelHandlerContext ctx;
		public HeartBeatTask(ChannelHandlerContext ctx){
			this.ctx = ctx;
		}
		@Override
		public void run() {
			sendPingMsg(ctx);
		}
	}

	protected void sendPingMsg(ChannelHandlerContext ctx) {
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.HEARTBEAT_REQ.getType());
		heartbeatCount++;
		ctx.writeAndFlush(message);
		System.out.println("client->sent ping msg to " + ctx.channel().remoteAddress() + ", count: " + heartbeatCount);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.err.println("---" + ctx.channel().remoteAddress() + " is inactive, do connect again---");
		if(heartBeat != null){
			heartBeat.cancel(true);
			heartBeat = null;
		}
		client.doConnect(client.getHost(), client.getPort());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(heartBeat != null){
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
	
	
}
