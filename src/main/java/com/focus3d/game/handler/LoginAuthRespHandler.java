package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.card.database.UserDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.handler.logic.card.GetCardLogic;
import com.focus3d.game.protocal.GameMessage;
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
			String userid = "";
			if(jo.containsKey("userid")){
				userid = jo.getString("userid");
			}
			//验证用户
			User user = UserDB.select(userid);
			if(user != null){
				user.setChannel(ctx.channel());
				//加入组
				Group group = GroupDB.join(user);
				log.info("user:" + user.toString() + " 加入组[" + group.toString() + "]");
				ctx.writeAndFlush(buildLoginResp(0, user, group));
				sendSelfInfoToOther(user, group);
			} else {
				ctx.writeAndFlush(buildLoginResp(-1, null, null));
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		String log = channel.remoteAddress().toString() + ">> connect.";
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.CONNECT_ACTIVE_RESP.getType());
		message.setBody(new JSONObject());
		channel.writeAndFlush(message);
		//暂时
		/*User user = UserDB.select("1");
		if(user != null){
			user.setChannel(ctx.channel());
			//加入组
			Group group = GroupDB.join(user);
			GetCardLogic.getCard(ctx, null);
		}*/
		System.out.println(log);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//玩家退出
		GroupDB.delete(ctx.channel());
	}

	private GameMessage buildLoginResp(int loginStatus, User currentUser, Group group) {
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.LOGIN_RESP.getType());
		JSONObject jo = new JSONObject();
		jo.put("status", loginStatus);
		jo.put("groupid", group.getId());
		jo.put("userid", currentUser.getId());
		jo.put("sex", currentUser.getSex());
		jo.put("seat", currentUser.getSeatNo());
		JSONArray jsonArray = new JSONArray();
		if(currentUser != null){
			List<User> userList = group.getUserList();
			if(userList.size() > 1){
				for (User user : userList) {
					if(!user.equals(currentUser)){
						JSONObject otherJo = new JSONObject();
						otherJo.put("userid", user.getId());
						otherJo.put("sex", user.getSex());
						otherJo.put("seat", user.getSeatNo());
						jsonArray.add(otherJo);
					}
				}
			}
		}
		jo.put("other", jsonArray);
		message.setBody(jo);
		return message;
	}
	/**
	 * 把自己信息推送给其他玩家
	 * *
	 */
	private void sendSelfInfoToOther(User currentUser, Group group){
		JSONObject jo = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject otherJo = new JSONObject();
		otherJo.put("groupid", group.getId());
		otherJo.put("userid", currentUser.getId());
		otherJo.put("sex", currentUser.getSex());
		otherJo.put("seat", currentUser.getSeatNo());
		jsonArray.add(otherJo);
		jo.put("other", jsonArray);
		List<User> userList = group.getUserList();
		if(userList.size() > 1){
			for (User user : userList) {
				if(!user.equals(currentUser)){
					Channel channel = user.getChannel();
					GameMessage message = new GameMessage();
					message.getHeader().setType((byte)MessageType.USER_JOIN_RESP.getType());
					message.setBody(jo);
					channel.writeAndFlush(message);
				}
			}
		}
	}
	
}
