package com.focus3d.game.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focus3d.game.card.Card;
import com.focus3d.game.card.CardManager;
import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.handler.logic.card.GetCardLogic;
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
				GetCardLogic.getCard(ctx, message);
			} else if(message.getHeader().getType() == MessageType.CARD_SEND_REQ.getType()){
				//打牌
				String body = String.valueOf(message.getBody());
				if(!StringUtil.isNullOrEmpty(body)){
					JSONObject bodyJo = JSONObject.fromObject(body);
					String sendCardUserId = bodyJo.getString("userid");
					String sendCard = bodyJo.getString("card");
					if(!StringUtil.isNullOrEmpty(sendCardUserId) && !StringUtil.isNullOrEmpty(sendCard)){
						System.out.println("玩家:" + sendCardUserId + "发牌：" + sendCard);
						Group group = GroupDB.select(ctx.channel());
						List<User> userList = group.getUserList();
						for (User user : userList) {
							//计算剩余牌
							if(user.getId().equals(sendCardUserId)){
								Integer remainCard = user.getCard().getRemainCard();
								if(remainCard > 0){
									user.getCard().setRemainCard(remainCard - JSONArray.fromObject(sendCard).size());
								}
							}
							System.out.println("玩家:" + user.getId() + ",收到玩家：" + sendCardUserId + "的牌：" + sendCard);
							user.getChannel().writeAndFlush(buildCardSendResp(sendCardUserId, user, sendCard));
						}
					}
				}
			} else if(message.getHeader().getType() == MessageType.USER_ROB_HOST_REQ.getType()){ 
				String body = String.valueOf(message.getBody());
				if(!StringUtil.isNullOrEmpty(body)){
					JSONObject bodyJo = JSONObject.fromObject(body);
					String userId = bodyJo.getString("userid");
					int station = bodyJo.getInt("station");//0-不抢 1-抢
					Group group = GroupDB.select(ctx.channel());
					List<User> userList = group.getUserList();
					//初始化计数器
					int initCount = 0;
					for (User user : userList) {
						Integer clickCount = user.getCard().getRobHostClickCount();
						if(clickCount != null){
							initCount += clickCount;
						}
					}
					//设置玩家是否抢地主计数
					for (User user : userList) {
						if(userId.equals(user.getId())){
							Integer clickCount = user.getCard().getRobHostClickCount();
							if(station == 0){
								if(clickCount == null){
									user.getCard().setRobHostClickCount(0);
								}
							} else {
								//叫牌者第二次抢牌
								if(user.getCard().isCaller() && user.getCard().getRobHostClickCount() != null){
									user.getCard().setSecondClick(true);
								}
								user.getCard().setRobHostClickCount(++ initCount);
							}
						}
					}
					//玩家是否都确认过
					boolean isAllClick = true;
					for (User user : userList) {
						if(user.getCard().getRobHostClickCount() == null){
							isAllClick = false;
							break;
						}
					}
					if(isAllClick){
						String hostUserid = "";
						//验证谁抢得了地主
						if(initCount == 0){
							//重新洗牌
							
						} else if(initCount == 1) {
							//叫地主玩家既是地主
							for (User user : userList){
								if(user.getCard().isCaller()){
									hostUserid = user.getId();
									break;
								}
							}
						} else if(initCount == 3) {
							//有2个玩家抢地主，或者叫牌者第二次不要牌
							boolean isSecondCall = false;
							for (User user : userList){
								if(user.getCard().isCaller() && user.getCard().isSecondClick()){
									isSecondCall = true;
									hostUserid = user.getId();
									break;
								}
							}
							if(!isSecondCall){
								//通知叫地主玩家第二次抢牌
								
							} else {
								for (User user : userList){
									if(user.getCard().getRobHostClickCount() == 2){
										hostUserid = user.getId();
										break;
									}
								}
							}
						} else if(initCount == 6) {
							//有2个玩家抢地主，叫牌者第二次点击抢地主
							for (User user : userList){
								if(user.getCard().isCaller() && user.getCard().isSecondClick()){
									hostUserid = user.getId();
									break;
								}
							}
						} else if(initCount == 7) {
							//有3个玩家抢地主，或者叫牌玩家第二次不要牌
							boolean isSecondCall = false;
							for (User user : userList){
								if(user.getCard().isCaller() && user.getCard().isSecondClick()){
									isSecondCall = true;
									hostUserid = user.getId();
									break;
								}
							}
							if(!isSecondCall){
								//通知叫地主玩家第二次抢牌
								
							} else {
								for (User user : userList){
									if(user.getCard().getRobHostClickCount() == 4){
										hostUserid = user.getId();
										break;
									}
								}
							}
							
						} else if(initCount == 14) {
							//有3个玩家抢地主，叫牌者第二次点击抢地主
							for (User user : userList){
								if(user.getCard().isCaller() && user.getCard().isSecondClick()){
									hostUserid = user.getId();
									break;
								}
							}
						}
						if(!StringUtil.isNullOrEmpty(hostUserid)){
							System.out.println("玩家：" + hostUserid + " 抢得了本轮的地主。");
						}
					} else {
						System.out.println("还有玩家没有点击是否要地主");
					}
				}
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
		
		/**
		 * 打牌
		 * *
		 * @param sendUserId 出牌者id
		 * @param card 出牌者出的牌
		 * @return
		 */
		private GameMessage buildCardSendResp(String userId, User user, String card) {
			JSONObject jo = new JSONObject();
			jo.put("userid", userId);
			jo.put("card", card);
			jo.put("remain", user.getCard().getRemainCard());
			GameMessage message = new GameMessage();
			message.getHeader().setType((byte)MessageType.CARD_SEND_RESP.getType());
			message.setBody(jo + "\0");
			return message;
		}
		
}
