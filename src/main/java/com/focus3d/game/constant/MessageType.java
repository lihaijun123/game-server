package com.focus3d.game.constant;

public enum MessageType {
	BUSINESS_REQ(0, "业务请求消息"),
	BUSINESS_RESP(1, "业务响应消息"),
	LOGIN_REQ(3, "握手请求消息"),
	LOGIN_RESP(4, "握手响应消息"),
	HEARTBEAT_REQ(5, "心跳请求消息"),
    HEARTBEAT_RESP(6, "心跳响应消息");
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
