package com.focus3d.game.game.protocal;

public class GameMessage {
	private Header header = new Header();
	private Object body;
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "header:" + header.toString() + (body != null ? ",body:" + body.toString() : "");
	}
}
