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
import com.focus3d.game.game.protocal.GameMessage;
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
				//发牌
				Group group = GroupDB.select(ctx.channel());
				List<User> userList = group.getUserList();
				if(userList.size() == 3){
					String body = String.valueOf(message.getBody());
					if(!StringUtil.isNullOrEmpty(body)){
						JSONObject bodyJo = JSONObject.fromObject(body);
						
					}
					CardManager cardManager = new CardManager();
					Map<String, Card> shuffleCards = cardManager.ShuffleCards();
					Card bootomCard = shuffleCards.get(CardManager.PLAYER_KEY_BOTTOM_CARD);
					for(int i = 0; i < userList.size(); i ++){
						User user = userList.get(i);
						Card card = shuffleCards.get(CardManager.PLAYER_KEY_PREFIX + (i + 1));
						user.setCard(card);
						user.setBootomCard(bootomCard);
						user.setRemainCard(card.getData().split(",").length);
					}
					for(User user : userList){
						GameMessage cardGetResp = buildCardGetResp(MessageType.CARD_PUSH_RESP, user, userList, group);
						user.getChannel().writeAndFlush(cardGetResp);
					}
					//随机选出一个叫地主玩家
					User callHostUser = userList.get((new Random()).nextInt(userList.size()));
					callHostUser.getCard().setCaller(true);
					System.out.println("玩家：" + callHostUser.toString() + " 被选出叫地主权利。");
					GameMessage cardGetResp = buildCallHostResp(callHostUser);
					callHostUser.getChannel().writeAndFlush(cardGetResp);
				} else {
					System.out.println("组[" + group.getId() + "]成员数：" + userList.size() + " 小于3，不可以发牌");
					ctx.writeAndFlush(buildCardGetResp(MessageType.CARD_GET_RESP, null, null, null));
				}
			} else if(message.getHeader().getType() == MessageType.CARD_SEND_REQ.getType()){
				//打牌
				String body = String.valueOf(message.getBody());
				if(!StringUtil.isNullOrEmpty(body)){
					JSONObject bodyJo = JSONObject.fromObject(body);
					String userId = bodyJo.getString("userid");
					String card = bodyJo.getString("card");
					if(!StringUtil.isNullOrEmpty(userId) && !StringUtil.isNullOrEmpty(card)){
						System.out.println("玩家:" + userId + "发牌：" + card);
						Group group = GroupDB.select(ctx.channel());
						List<User> userList = group.getUserList();
						for (User user : userList) {
							//计算剩余牌
							if(user.getId().equals(userId)){
								Integer remainCard = user.getRemainCard();
								if(remainCard > 0){
									user.setRemainCard(remainCard - JSONArray.fromObject(card).size());
								}
							}
							System.out.println("玩家:" + user.getId() + ",收到玩家：" + userId + "的牌：" + card);
							user.getChannel().writeAndFlush(buildCardSendResp(userId, user, card));
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
		 * 发牌
		 * *
		 * @param jo
		 * @return
		 */
		private GameMessage buildCardGetResp(MessageType messageType, User currentUser , List<User> userList, Group group) {
			JSONObject jo = new JSONObject();
			if(currentUser != null){
				Card card = currentUser.getCard();
				Card bootomCard = currentUser.getBootomCard();
				jo.put("groupid", group.getId());
				jo.put("userid", currentUser.getId());
				jo.put("card", card.getData());
				jo.put("dp", bootomCard.toString());
				JSONArray jsonArray = new JSONArray();
				for(User user : userList){
					if(!user.equals(currentUser)){
						JSONObject otherJo = new JSONObject();
						otherJo.put("userid", user.getId());
						otherJo.put("card", user.getCard().getData());
						jsonArray.add(otherJo);
					}
				}
				jo.put("other", jsonArray.toString());
			}
			GameMessage message = new GameMessage();
			message.getHeader().setType((byte)messageType.getType());
			message.setBody(jo);
			return message;
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
			jo.put("remain", user.getRemainCard());
			GameMessage message = new GameMessage();
			message.getHeader().setType((byte)MessageType.CARD_SEND_RESP.getType());
			message.setBody(jo + "\0");
			return message;
		}
		/**
		 * 随机分配给玩家叫地主
		 * *
		 * @param user
		 * @return
		 */
		private GameMessage buildCallHostResp(User user) {
			JSONObject jo = new JSONObject();
			jo.put("userid", user.getId());
			GameMessage message = new GameMessage();
			message.getHeader().setType((byte)MessageType.USER_ROB_HOST_CALL_RESP.getType());
			message.setBody(jo);
			return message;
		}
		public static void main(String[] args){
			String s = "[\"3_11\",\"2_11\",\"2_10\"]";
			JSONObject jo = new JSONObject();
			JSONArray fromObject = JSONArray.fromObject(s);
			System.out.println(fromObject.size());
		}
	    
}
