package com.focus3d.game.card;

import io.netty.channel.Channel;

/**
 * 玩家
 * *
 * @author lihaijun
 *
 */
public class User {
	private String id;
	private String name;
	private Channel channel;
	private Integer sex = 1;//0-女 1-男
	private Integer seatNo;//座位号
	private Card card = new Card("");
	public User(String id, String name){
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	@Override
	public int hashCode() {
		int result = 37;
		result = result * 16 + name.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User){
			User target = (User)obj;
			return getId().equals(target.getId());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getId().toString();
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(Integer seatNo) {
		this.seatNo = seatNo;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}
	
}
