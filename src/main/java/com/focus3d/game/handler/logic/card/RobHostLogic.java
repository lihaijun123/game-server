package com.focus3d.game.handler.logic.card;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import com.focus3d.game.card.Card;
import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.database.GroupDB;
import com.focus3d.game.constant.MessageType;
import com.focus3d.game.protocal.GameMessage;

/**
 * 抢地主逻辑
 * *
 * @author lihaijun
 *
 */
public class RobHostLogic {
	/**
	 * 
	 * *
	 * @param ctx
	 * @param message
	 */
	public static void rotHost(ChannelHandlerContext ctx, GameMessage message){
		//叫地主
		String body = String.valueOf(message.getBody());
		if(!StringUtil.isNullOrEmpty(body)){
			JSONObject bodyJo = JSONObject.fromObject(body);
			String currentUserId = bodyJo.getString("userid");
			int station = bodyJo.getInt("station");//0-不抢 1-抢
			Group group = GroupDB.select(ctx.channel());
			List<User> userList = group.getUserList();
			//叫地主是否第二次抢地主
			boolean isSecondCall = false;
			//设置玩家是否抢地主标志
			User currentUser = getUser(currentUserId, userList);
			System.out.println("玩家：" + currentUserId + " " + (station == 1 ? (currentUser.getCard().isCaller() ? "叫" : "抢") + "地主" : "不要地主"));
			Integer clickCount = currentUser.getCard().getRobHostClick();
			if(clickCount == null){
				currentUser.getCard().setRobHostClick(station);
			} else {
				//叫牌者第二次抢牌
				if(currentUser.getCard().isCaller()){
					isSecondCall = true;
				}
			}
			//玩家是否都确认过
			boolean isAllClick = true;
			for (User user : userList) {
				if(user.getCard().getRobHostClick() == null){
					isAllClick = false;
					break;
				}
			}
			boolean isCall = false;
			for (User user : userList) {
				if(user.getCard().getRobHostClick() != null && user.getCard().getRobHostClick() == 1){
					isCall = true;
					break;
				}
			}
			if(isAllClick){
				String hostUserid = "";
				//初始化计数器
				int robHostUserCount = 0;
				for (User user : userList) {
					Integer count = user.getCard().getRobHostClick();
					if(count != null){
						robHostUserCount += count;
					}
				}
				//验证谁抢得了地主
				if(robHostUserCount == 0){
					//重新洗牌
					System.out.println("一轮下来，没有人想抢地主，需要重新洗牌");
				} else if(robHostUserCount == 1){
					hostUserid = findPrevRobHostUser(currentUserId, userList).getId();
				} else {
					if(isSecondCall){
						if(clickCount == 1 && station == 1){
							System.out.println("叫地主玩家 " + currentUser + " 第二次抢地主。");
							hostUserid = currentUserId;
						} else {
							hostUserid = findPrevRobHostUser(currentUserId, userList).getId();
						}
					} else {
						User caller = getRobHostCaller(userList);
						if(caller.getCard().getRobHostClick() != 0){
							//通知叫地主玩家再次叫地主
							System.out.println("通知叫地主玩家 " + caller + " 再次叫地主");
							GameMessage callHostResp = GetCardLogic.buildCallHostResp(caller);
							caller.getChannel().writeAndFlush(callHostResp);
							//向所有人发
							notifyAllUserResp(userList, currentUserId, station, hostUserid, isCall);
						} else {
							//上家抢地主玩家是地主
							hostUserid = findPrevRobHostUser(currentUserId, userList).getId();
						}
					}
				}
				if(!StringUtil.isNullOrEmpty(hostUserid)){
					System.out.println("玩家：" + hostUserid + " 抢得了本轮的地主。");
					//把底牌给地主玩家
					User hostUser = getUser(hostUserid, userList);
					Integer hostUserCardNum = hostUser.getCard().getRemainCard();
					Integer hostUserBtCardNum = hostUser.getCard().getBootomCard().getData().split(",").length;
					hostUser.getCard().setRemainCard(hostUserCardNum + hostUserBtCardNum);
					notifyAllUserResp(userList, currentUserId, station, hostUserid, isCall);
				}
			} else {
				System.out.println("还有玩家没有点击是否要地主");
				notifyAllUserResp(userList, currentUserId, station, "", isCall);
				//通知下家叫地主
				User nextUser = nextUser(currentUserId, userList);
				System.out.println("轮到： " + nextUser.toString() + " 叫地主");
				GameMessage callHostResp = GetCardLogic.buildCallHostResp(nextUser);
				nextUser.getChannel().writeAndFlush(callHostResp);
			}
		}
	
	}
	
	private static void notifyAllUserResp(List<User> userList, String currentUserId, int station, String hostUserid, boolean isCall){
		for(User user : userList){
			GameMessage robHostResp = buildRobHostResp(currentUserId, station, hostUserid, isCall);
			user.getChannel().writeAndFlush(robHostResp);
		}
	}
	
	/**
	 * 抢地主响应
	 * *
	 * @param userId
	 * @param station
	 * @param targetUserId
	 * @return
	 */
	private static GameMessage buildRobHostResp(String userId, int station, String targetUserId, boolean isCall){
		JSONObject jo = new JSONObject();
		jo.put("userid", userId);
		jo.put("station", station);
		jo.put("targetuserid", targetUserId);
		jo.put("iscall", isCall ? 1 : 0);
		GameMessage message = new GameMessage();
		message.getHeader().setType((byte)MessageType.USER_ROB_HOST_RESP.getType());
		message.setBody(jo + "\0");
		return message;
	}
	
	/**
	 * 下家
	 * *
	 * @param userId
	 * @param userList
	 * @return
	 */
	public static User nextUser(String userId, List<User> userList){
		//通知下家叫地主
		int nextUserIndex = 0;
		for(User user : userList){
			if(userId.equals(user.getId())){
				int indexOf = userList.indexOf(user);
				if(indexOf != userList.size() - 1){
					nextUserIndex = indexOf + 1;
				}
				break;
			}
		}
		return userList.get(nextUserIndex);
	}
	/**
	 * 上家
	 * *
	 * @param userId
	 * @param userList
	 * @return
	 */
	public static User prevUser(String userId, List<User> userList){
		//通知下家叫地主
		int prevUserIndex = 0;
		for(User user : userList){
			if(userId.equals(user.getId())){
				int indexOf = userList.indexOf(user);
				if(indexOf == 0){
					prevUserIndex = userList.size() -1;
				} else {
					prevUserIndex = indexOf - 1;
				}
				break;
			}
		}
		return userList.get(prevUserIndex);
	}
	/**
	 * 获取用户
	 * *
	 * @param userId
	 * @param userList
	 * @return
	 */
	public static User getUser(String userId, List<User> userList){
		for(User user : userList){
			if(userId.equals(user.getId())){
				return user;
			}
		}
		return null;
	}
	/**
	 * 找出前一个要地主的玩家
	 * *
	 * @param currentUserId
	 * @param userList
	 * @return
	 */
	private static User findPrevRobHostUser(String currentUserId, List<User> userList){
		User prevUser = prevUser(currentUserId, userList);
		String robHostUserId = "";
		Integer robHostClick = prevUser.getCard().getRobHostClick();
		if(robHostClick != null && robHostClick == 1){
			robHostUserId = prevUser.getId();
		}
		if(StringUtil.isNullOrEmpty(robHostUserId)){
			prevUser = findPrevRobHostUser(prevUser.getId(), userList);
		}
		return prevUser;
	}
	
	/**
	 * 获取叫地主玩家
	 * *
	 * @param userList
	 * @return
	 */
	private static User getRobHostCaller(List<User> userList) {
		User caller = null;
		for(User user : userList){
			if(user.getCard().isCaller()){
				caller = user;
				break;
			}
		}
		return caller;
	}
	

	public static void main(String[] arg){
		List<User> userlist = new ArrayList<User>();
		User e1 = new User("1", "test1");
		e1.setCard(new Card("dsfsd"));
		e1.getCard().setRobHostClick(1);
		userlist.add(e1);
		User e2 = new User("2", "test2");
		e2.setCard(new Card("dsf"));
		userlist.add(e2);
		User e3 = new User("3", "test3");
		e3.setCard(new Card("dsf"));
		userlist.add(e3);
		User e4 = new User("4", "test4");
		e4.setCard(new Card("dsfdf"));
		e4.getCard().setRobHostClick(1);
		userlist.add(e4);
		User findPrevRobHostUser = findPrevRobHostUser("3", userlist);
		System.out.println(findPrevRobHostUser);
	}
}
