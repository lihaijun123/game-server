package com.focus3d.game.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import com.focus3d.game.codec.MessageDecoder;
import com.focus3d.game.codec.MessageEncoder;
import com.focus3d.game.handler.GameServerHandler;
import com.focus3d.game.handler.HeartBeatRespHandler;
import com.focus3d.game.handler.LoginAuthRespHandler;

/**
 * Hello world!
 *
 */
public class GameServer 
{
	private int port;
	
	
    public GameServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 ch.pipeline().addLast(new LoggingHandler(LogLevel.ERROR));
                	 ch.pipeline().addLast(new ReadTimeoutHandler(600));
                	 ch.pipeline().addLast(new MessageDecoder(1024 * 1024 * 10, 0, 4));
                	 ch.pipeline().addLast(new MessageEncoder());
                	 ch.pipeline().addLast(new LoginAuthRespHandler());
                     ch.pipeline().addLast(new GameServerHandler());
                     ch.pipeline().addLast(new HeartBeatRespHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
            if(f.isSuccess()){
            	System.out.println("服务器启动成功！");
            }
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new GameServer(port).run();
    }
}
