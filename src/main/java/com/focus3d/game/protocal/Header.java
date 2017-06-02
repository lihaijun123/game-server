package com.focus3d.game.protocal;

public class Header {
	private int crcCode = 0xabef0101;
	private int length;//整包长度
	private int sessionID;
	private byte type;//消息类型
	private byte priority;
	public int getCrcCode() {
		return crcCode;
	}
	public void setCrcCode(int crcCode) {
		this.crcCode = crcCode;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getSessionID() {
		return sessionID;
	}
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public byte getPriority() {
		return priority;
	}
	public void setPriority(byte priority) {
		this.priority = priority;
	}
	@Override
	public String toString() {
		return "type:" + getType() + ",sessionId:" + getSessionID()+ ",length:" + getLength();
	}
}
