package com.focus3d.game.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.json.JSONObject;

import com.focus3d.game.client.GameClient;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;
/**
 * *
 * @author lihaijun
 *
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
	
	private GameClient gameClient;

	public LoginAuthReqHandler(GameClient gameClient) {
		this.gameClient = gameClient;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		GameMessage message = buildLoginReq();
		ctx.writeAndFlush(message);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GameMessage message = (GameMessage)msg;
		if(msg != null && message.getHeader().getType() == MessageType.LOGIN_RESP.getType()){
			Object body = message.getBody();
			JSONObject jo = JSONObject.fromObject(body);
			if(jo.containsKey("loginStatus")){
				int status = jo.getInt("loginStatus");
				if(0 == status){
					System.out.println("login is ok:" + message);
					gameClient.isLogin = true;
					ctx.fireChannelRead(msg);
				} else {
					ctx.close();
					System.out.println("握手失败，关闭连接");
				}
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		ctx.close();
	}

	private GameMessage buildLoginReq() {
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.LOGIN_REQ.getType());
		JSONObject jo = new JSONObject();
		jo.put("username", "admin");
		jo.put("password", "test");
		message.setBody(jo);
		return message;
	}

	
	
}
