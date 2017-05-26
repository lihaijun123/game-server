package com.focus3d.game.card.database;

import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

import com.focus3d.game.card.User;

/**
 * 
 * *
 * @author lihaijun
 *
 */
public class UserDB {

	private static List<User> data = new ArrayList<User>();
	
	static {
		for(int i = 0; i < 20; i ++){
			User user = new User(String.valueOf(i), "user_" + i);
			data.add(user);
		}
	}
	
	public static User select(String userId){
		User user = null;
		if(!StringUtil.isNullOrEmpty(userId)){
			for (User u : data) {
				if(String.valueOf(u.getId()).equals(userId)){
					user = u;
					break;
				}
			}
		}
		return user;
	}
	
}
