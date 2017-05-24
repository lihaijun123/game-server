package com.focus3d.game.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.sf.json.JSONObject;

import com.focus3d.game.game.protocal.GameMessage;
import com.focus3d.game.game.protocal.Header;
import com.focus3d.game.handler.GameServerHandler;

public class MessageDecoder extends LengthFieldBasedFrameDecoder {
	
	public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, 5 , 0);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf)super.decode(ctx, in);
		if(frame == null){
			return null;
		}
		int length = frame.readInt();
		int sessionId = frame.readInt();
		byte type = frame.readByte();
		byte[] mb = new byte[frame.readableBytes()];
		//System.out.println("length->" + length);
		System.out.println("sessionId->" + sessionId);
		System.out.println("type->" + type);
		System.out.println("bodyLength->" + mb.length);
		System.out.println("channels->" + GameServerHandler.channels.size());
		frame.readBytes(mb);
		String body = new String(mb, "UTF-8");
		GameMessage message = new GameMessage();
		Header header = new Header();
		//header.setLength(length);
		header.setSessionID(sessionId);
		header.setType(type);
		message.setHeader(header);
		message.getHeader().setLength(length);
		if(!"".equals(body)){
			JSONObject jo = JSONObject.fromObject(body);
			message.setBody(jo);
			
		}
		return message;
	}
	
	public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }
}
