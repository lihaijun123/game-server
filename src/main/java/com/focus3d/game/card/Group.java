package com.focus3d.game.card;

import java.util.ArrayList;
import java.util.List;

/**
 * 斗地主玩家组
 * *
 * @author lihaijun
 *
 */
public class Group {

	private Long id;
	private String name;
	private List<User> userList = new ArrayList<User>();
	private CardManager card;
	public Group(Long id, String name){
		this.id = id;
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	@Override
	public int hashCode() {
		int result = 37;
		result = result * 16 + name.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Group){
			Group target = (Group)obj;
			return getId().equals(target.getId());
		}
		return false;
	}
	@Override
	public String toString() {
		return getId() + "," + getName();
	}
	public CardManager getCard() {
		return card;
	}
	public void setCard(CardManager card) {
		this.card = card;
	}
}
