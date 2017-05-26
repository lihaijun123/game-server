package com.focus3d.game.constant;
/**
 * 消息类型
 * *
 * @author lihaijun
 *
 */
public enum MessageType {
	BUSINESS_REQ(0, "业务请求消息"),
	BUSINESS_RESP(1, "业务响应消息"),
	HEARTBEAT_REQ(5, "心跳请求消息"),
	HEARTBEAT_RESP(6, "心跳响应消息"),
	CONNECT_ACTIVE_RESP(2, "客户端建立连接"),
	LOGIN_REQ(3, "登录请求消息"),
	LOGIN_RESP(4, "登录响应消息"),
	CARD_GET_REQ(10, "发牌请求消息"),
	CARD_GET_RESP(11, "发牌响应消息"),
	CARD_SEND_REQ(12, "玩家出牌请求消息"),
	CARD_SEND_RESP(13, "玩家出牌响应消息"),
	CARD_PUSH_RESP(14, "玩家满3位推牌响应消息"),
	USER_JOIN_RESP(21, "玩家进入加入游戏响应消息"),
	USER_ROB_HOST_REQ(22, "玩家抢地主请求消息"),
	USER_ROB_HOST_RESP(23, "玩家抢地主响应消息");
	
    int type;
	String name;
	MessageType(int type, String name){
		this.type = type;
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
