package com.focus3d.game.game.protocal;
/**
 * 消息协议
 * *
 * @author lihaijun
 *
 */
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
		return "header-[" + header.toString() + "]" + "#body-[" + (body != null ? body.toString() : "") + "]";
	}
}
