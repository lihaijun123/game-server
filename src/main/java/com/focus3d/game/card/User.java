package com.focus3d.game.card;

import io.netty.channel.Channel;

/**
 * 玩家
 * *
 * @author lihaijun
 *
 */
public class User {

	private Long id;
	private String name;
	private Card card;
	private Channel channel;
	private String loginUserName;
	private String loginPassword;
	
	public User(String name, String loginUserName, String loginPassword){
		this.name = name;
		this.loginUserName = loginUserName;
		this.loginPassword = loginPassword;
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
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
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
	public String getLoginUserName() {
		return loginUserName;
	}
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}
	public String getLoginPassword() {
		return loginPassword;
	}
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
	
}
