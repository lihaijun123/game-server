package com.focus3d.game.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.focus3d.game.game.protocal.GameMessage;

public class MessageEncoder extends MessageToByteEncoder<GameMessage>{

	@Override
	protected void encode(ChannelHandlerContext ctx, GameMessage message, ByteBuf out) throws Exception {
		Object body = message.getBody();
		int length = 0;
		if(body != null){
			length = body.toString().getBytes("UTF-8").length;
		}
		System.out.println("length->" + length);
		out.writeBytes(int2Byte(length));//整包长度
		int sessionID = message.getHeader().getSessionID();
		out.writeInt(sessionID);//sessionId
		System.out.println("sessionID->" + sessionID);
		byte type = message.getHeader().getType();
		System.out.println("type->" + type);
		out.writeByte(type);//消息类型
		byte[] bodyBytes = new byte[0];
		if(body != null){
			bodyBytes = body.toString().getBytes("UTF-8");
		}
		System.out.println("bodyLength->" + bodyBytes.length);
		out.writeBytes(bodyBytes);
	}

	 public static byte[] int2Byte(int intValue) {
	        byte[] b = new byte[4];
	        for (int i = 0; i < 4; i++) {
	            b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
	        }
	        return b;
	    }


}
