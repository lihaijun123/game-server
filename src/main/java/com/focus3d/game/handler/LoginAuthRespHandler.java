package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.card.database.UserDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;
/**
 * 登录验证
 * *
 * @author lihaijun
 *
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(LoginAuthRespHandler.class);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		GameMessage message = (GameMessage)msg;
		if(message != null && message.getHeader().getType() == MessageType.LOGIN_REQ.getType()){
			Object body = message.getBody();
			JSONObject jo = JSONObject.fromObject(body);
			String userName = "";
			String password = "";
			if(jo.containsKey("username")){
				userName = jo.getString("username");
			}
			if(jo.containsKey("password")){
				password = jo.getString("password");
			}
			User user = UserDB.select(userName, password);
			if(user != null){
				user.setChannel(ctx.channel());
				//加入组
				Group group = GroupDB.join(user);
				log.info("user:" + user.toString() + "join in group[" + group.toString() + "]");
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
		Channel channel = ctx.channel();
		String log = channel.remoteAddress().toString() + ">> connect.";
		System.out.println(log);
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
