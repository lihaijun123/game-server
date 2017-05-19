package com.focus3d.game.card.database;

import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

import com.focus3d.game.card.User;
import com.focus3d.game.card.utils.IdGenerateUtils;

/**
 * 
 * *
 * @author lihaijun
 *
 */
public class UserDB {

	private static List<User> data = new ArrayList<User>();
	
	static {
		User user = new User(IdGenerateUtils.getId(), "player" + data.size(), "admin", "test");
		data.add(user);
		User user2 = new User(IdGenerateUtils.getId(), "player2" + data.size(), "admin", "test2");
		data.add(user2);
		User user3 = new User(IdGenerateUtils.getId(), "player3" + data.size(), "admin", "test3");
		data.add(user3);
	}
	
	public static User select(String loginUserName, String password){
		User user = null;
		if(!StringUtil.isNullOrEmpty(loginUserName) && !StringUtil.isNullOrEmpty(password)){
			for (User u : data) {
				if(u.getLoginUserName().equals(loginUserName) && u.getLoginPassword().equals(password)){
					user = u;
					break;
				}
			}
		}
		return user;
	}
	
}
