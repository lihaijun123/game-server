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
		System.out.println("###### decode sta ######");
		ByteBuf frame = (ByteBuf)super.decode(ctx, in);
		if(frame == null){
			return null;
		}
		int length = frame.readInt();
		int sessionId = frame.readInt();
		byte type = frame.readByte();
		byte[] mb = new byte[frame.readableBytes()];
		frame.readBytes(mb);
		String body = new String(mb, "UTF-8");
		GameMessage message = new GameMessage();
		Header header = new Header();
		header.setSessionID(sessionId);
		header.setType(type);
		message.setHeader(header);
		message.getHeader().setLength(length);
		if(!"".equals(body)){
			JSONObject jo = JSONObject.fromObject(body);
			message.setBody(jo);
			
		}
		System.out.println("length->" + mb.length);
		System.out.println("sessionId->" + sessionId);
		System.out.println("type->" + type);
		System.out.println("body->" + body);
		System.out.println("channels->" + GameServerHandler.channels.size());
		System.out.println("###### decode end ######");
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
