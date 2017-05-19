package com.focus3d.game.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.focus3d.game.constant.MessageType;
import com.focus3d.game.game.protocal.GameMessage;

public class GameClientHandler extends ChannelInboundHandlerAdapter {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
       GameMessage message = (GameMessage)msg;
       if(message.getHeader().getType() == MessageType.BUSINESS_RESP.getType()){
    	   //业务处理
    	   System.out.println("服务器业务响应->" + msg.toString());
       } else if(message.getHeader().getType() == MessageType.CARD_GET_RESP.getType()){
    	   System.out.println("服务器业务响应->" + msg.toString());
       } else {
    	   ctx.fireChannelRead(msg);
       }
    }
	 
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	/*JSONObject bodyJo = new JSONObject();
		bodyJo.put("age", 30);
		bodyJo.put("name", "数据包测试");
		for(int i = 0; i < 1000; i ++){
	    	bodyJo.put("id", i);
			GameMessage msg = new GameMessage();
			msg.getHeader().setSessionID(i);
			msg.getHeader().setType((byte)0);
			msg.setBody(bodyJo);
			ctx.writeAndFlush(msg);
		}*/
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
