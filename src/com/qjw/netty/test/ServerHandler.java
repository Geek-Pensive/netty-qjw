package com.qjw.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		try {
			//服务端 处理 client端发送的数据
			ByteBuf buf = (ByteBuf)msg;
			byte [] data = new byte[buf.readableBytes()];//可用的数据大小
			buf.readBytes(data);
			String request = new String(data, "utf-8");
			System.out.println("我是服务端：客户端说："+request);
			
			ctx.writeAndFlush(Unpooled.copiedBuffer("你好qjw".getBytes()))
				.addListener(ChannelFutureListener.CLOSE);//监听服务端响应数据后立即断开
			
			
		//不需要释放msg，因为如果要继续write，netty框架自动释放。
//		} finally {
//			ReferenceCountUtil.release(msg);
//		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
