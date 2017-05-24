package com.focus3d.game.handler;

import java.util.Random;

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
			if(jo.containsKey("status")){
				int status = jo.getInt("status");
				if(0 == status){
					System.out.println("玩家登录:" + message);
					gameClient.isLogin = true;
					ctx.fireChannelRead(msg);
				} else {
					ctx.close();
					System.out.println("握手失败，关闭连接");
				}
			}
		} else if(msg != null && message.getHeader().getType() == MessageType.USER_JOIN_RESP.getType()){
			System.out.println("其他玩家加入 :" + message);
		}
		else if(msg != null && message.getHeader().getType() == MessageType.CARD_GET_RESP.getType()){
			System.out.println("发牌响应 :" + message);
		}
		else {
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
		jo.put("userid", (new Random()).nextInt(10));
		message.setBody(jo);
		return message;
	}

	
	
}
