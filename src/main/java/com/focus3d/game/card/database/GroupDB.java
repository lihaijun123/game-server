package com.focus3d.game.card.database;

import java.util.ArrayList;
import java.util.List;

import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;

/**
 * 
 * *
 * @author lihaijun
 *
 */
public class GroupDB {
	
	public static List<Group> data = new ArrayList<Group>();

	public Group create(User user){
		
		Group group = new Group();
		group.getUserList().add(user);
		return group;
	}
}
