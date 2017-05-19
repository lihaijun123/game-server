package com.focus3d.game.card.database;

import com.focus3d.game.card.User;


import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * *
 * @author lihaijun
 *
 */
public class UserDB {

	private static List<User> data = new ArrayList<User>();
	
	static {
		User user = new User("player1", "admin", "test");
		User user2 = new User("player2", "admin", "test2");
		User user3 = new User("player3", "admin", "test3");
		data.add(user);
		data.add(user2);
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
