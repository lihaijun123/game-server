package com.focus3d.game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import com.focus3d.game.codec.MessageDecoder;
import com.focus3d.game.codec.MessageEncoder;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;
import com.focus3d.game.handler.GameClientHandler;
import com.focus3d.game.handler.HeartBeatReqHandler;
import com.focus3d.game.handler.LoginAuthReqHandler;

public class GameClient {
	EventLoopGroup workerGroup = new NioEventLoopGroup(4);
	private Bootstrap bootstrap;
	private Channel channel;
	private String host;
	private int port;
	public boolean isLogin = false;
	
/*	public static void main(String[] args) throws InterruptedException{
		GameClient client = new GameClient();
		client.start("172.17.13.27", 8080);
		Thread.sleep(1000000);
	}*/
	
	public void sendData(){
		JSONObject bodyJo = new JSONObject();
		int sessionId = (new Random()).nextInt(1000000);
		while(channel == null || !channel.isActive()){
			
		}
		for(int i = 0; i < 1; i ++){
			if(channel != null && channel.isActive()){
				GameMessage msg = new GameMessage();
			
				
				msg.getHeader().setSessionID(sessionId);
				msg.getHeader().setType((byte)MessageType.CARD_GET_REQ.getType());
				msg.setBody(bodyJo);
				System.out.println("发牌请求:" + msg);
				channel.writeAndFlush(msg);
				
				
				/*bodyJo = new JSONObject();
				bodyJo.put("userid", 1);
				bodyJo.put("card", "[\"3_11\",\"2_11\",\"2_10\"]");
				
				msg = new GameMessage();
				msg.getHeader().setSessionID(sessionId);
				msg.getHeader().setType((byte)MessageType.CARD_SEND_REQ.getType());
				msg.setBody(bodyJo);
				System.out.println("打牌请求:" + msg);
				channel.writeAndFlush(msg);
				*/
				bodyJo = new JSONObject();
				bodyJo.put("userid", 1);
				bodyJo.put("station", "0");
				
				msg = new GameMessage();
				msg.getHeader().setSessionID(sessionId);
				msg.getHeader().setType((byte)MessageType.USER_ROB_HOST_REQ.getType());
				msg.setBody(bodyJo);
				System.out.println("打牌请求:" + msg);
				channel.writeAndFlush(msg);
			}
		}
	}
	
	public void start(String host, int port) {
	        try {
	        	this.host = host;
	        	this.port = port;
	            bootstrap = new Bootstrap(); // (1)
	            bootstrap.group(workerGroup); // (2)
	            bootstrap.channel(NioSocketChannel.class); // (3)
	            bootstrap.option(ChannelOption.SO_KEEPALIVE, true); // (4)
	            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
	                @Override
	                public void initChannel(SocketChannel ch) throws Exception {
	                	ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
	                	ch.pipeline().addLast(new MessageDecoder(1024, 0, 4));
	                	ch.pipeline().addLast(new MessageEncoder());
	                	ch.pipeline().addLast(new LoginAuthReqHandler(GameClient.this));
	                	ch.pipeline().addLast(new GameClientHandler());
	                	//ch.pipeline().addLast(new HeartBeatReqHandler(GameClient.this));
	                }
	            });
	           doConnect(host, port);
	        } catch (Exception e) {
				throw new RuntimeException(e);
			}
	    }
	 
	 public void doConnect(final String host, final int port){
		 if(channel != null && channel.isActive()){
			 return;
		 }
		// Start the client.
		bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				if(f.isSuccess()){
					channel = f.channel();
					System.out.println("Connect to server successfully!");
				} else {
					System.out.println("Failed to connect to server, try connect after 5s");
					final EventLoop loop = f.channel().eventLoop();  
					loop.schedule(new Runnable() {
						@Override
						public void run() {
							doConnect(host, port);
						}
					}, 5, TimeUnit.SECONDS);
				}
			}
		});
	 }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
