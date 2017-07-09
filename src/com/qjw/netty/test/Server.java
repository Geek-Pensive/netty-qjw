package com.qjw.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	
	public static void main(String[] args) throws Exception {
		//1.第一个线程组 用于接收Client端连接
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//2.第二个线程组 用于实际的业务处理
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		//3.创建一个辅助类ServerBootstrap,对Server端进行一系列的配置
		ServerBootstrap sb = new ServerBootstrap();
		   //把两个工作线程组加入进来
		sb.group(bossGroup, workerGroup)
		   //指定tcp的通信通道，udp不同
		.channel(NioServerSocketChannel.class)
		   //指定具体的 事件处理器(我们自己写的处理器)
		.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast(new ServerHandler());
			}
		})
		  /*
		   * 服务器端TCP内核模块维护 2个队列， 假设为A、B
		   * 第一次握手：客户端想服务器端connect的时候，会发送带有SYN标志的包
		   * 第二次握手：服务器收到客户端发来的SYN时，想客户端发送SYN和 ACK确认
		   * 第三次握手：此时TCP内核模块 把客户端连接信息 加入A队列，并且服务端收到客户端ACK确认
		   * TCP内核模块把客户端连接从A移到B队列，连接完成！服务器accept方法返回SocketChannel，accept会从B中取出完成三次握手的客户端连接
		   * 
		   * A队列与B队列长度之和为 backlog，当A+B>backlog时，新的连接会被TCP内核拒绝
		   * 所以，如果backlog设置过小，可会能出现accept速度更不上，A,B队列满了，导致新的客户端无法连接
		   * 注意：backlog对程序支持的连接数无影响，只影响没有被accept取走的连接
		   */
		  //可以理解为：设置tcp缓冲区
		  .option(ChannelOption.SO_BACKLOG, 128)//可以不设置，默认为128
		  //使通道保持连接
		  .option(ChannelOption.SO_KEEPALIVE, true);
		
		//绑定端口，进行监听，异步
		ChannelFuture f = sb.bind(8765).sync();
		
		//相当于Thread.sleep(99999); 保持阻塞
		f.channel().closeFuture().sync();
		
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	
}
