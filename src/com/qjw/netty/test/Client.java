package com.qjw.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	public static void main(String[] args) throws Exception {
		
		//只需要一个线程组
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		 .channel(NioSocketChannel.class)
		 .handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast(new ClientHandler());
			}
		});
		
		ChannelFuture cf = b.connect("127.0.0.1", 8765).sync();
		
		
		//将数据放入缓冲区 buffer
		cf.channel().write(Unpooled.copiedBuffer("I am qjw".getBytes()));
		cf.channel().write(Unpooled.copiedBuffer("I like girl".getBytes()));
		cf.channel().flush(); // 将缓冲区的数据发送至服务端 
		//上面三行简化成：
//		cf.channel().writeAndFlush("I am qjw I like girl".getBytes());
		
		
		cf.channel().closeFuture().sync();
		group.shutdownGracefully();
		
		
	}
}
