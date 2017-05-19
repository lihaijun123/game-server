package com.focus3d.game.card.database;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.focus3d.game.card.Group;
import com.focus3d.game.card.User;
import com.focus3d.game.card.utils.IdGenerateUtils;

/**
 * 
 * *
 * @author lihaijun
 *
 */
public class GroupDB {
	
	public static List<Group> data = new ArrayList<Group>();

	public static Group join(User user){
		Group group = null;
		for(Group g : data){
			List<User> userList = g.getUserList();
			if(userList.contains(user)){
				group = g;
				break;
			}
		}
		if(group == null){	
			if(data.size() > 1){
				Collections.shuffle(data);
			}
			for(Group g : data){
				List<User> userList = g.getUserList();
				if(userList.size() < 3){
					group = g;
					break;
				}
			}
			if(group == null){
				group = new Group(IdGenerateUtils.getId(), "group_" + data.size());
				data.add(group);
			}
			//join in
			group.getUserList().add(user);
		}
		return group;
	}
	
	public static Group select(Channel channel){
		Group group = null;
		for(Group g : data){
			List<User> userList = g.getUserList();
			for(User u : userList){
				if(u.getChannel().id().toString().equals(channel.id().toString())){
					group = g;
					break;
				}
			}
			if(group != null){
				break;
			}
		}
		return group;
	}
}
