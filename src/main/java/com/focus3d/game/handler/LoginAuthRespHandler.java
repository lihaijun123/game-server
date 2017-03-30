package com.focus3d.game.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.json.JSONObject;

import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GameMessage message = (GameMessage)msg;
		if(message != null && message.getHeader().getType() == MessageType.LOGIN_REQ.getType()){
			Object body = message.getBody();
			JSONObject jo = JSONObject.fromObject(body);
			String userName = "";
			String password = "";
			if(jo.containsKey("userName")){
				userName = jo.getString("userName");
			}
			if(jo.containsKey("password")){
				password = jo.getString("password");
			}
			if(userName.equals("admin") && "test".equals(password)){
				ctx.writeAndFlush(buildLoginResp(0));
			} else {
				ctx.writeAndFlush(buildLoginResp(-1));
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress().toString() + ">> connect.");
	}



	private GameMessage buildLoginResp(int loginStatus) {
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.LOGIN_RESP.getType());
		JSONObject jo = new JSONObject();
		jo.put("loginStatus", loginStatus);
		message.setBody(jo);
		return message;
	}
}
