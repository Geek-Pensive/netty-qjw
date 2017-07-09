package com.qjw.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler  extends ChannelHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			ByteBuf buf = (ByteBuf)msg;
			byte [] data = new byte[buf.readableBytes()];//可用的数据大小
			buf.readBytes(data);
			String request = new String(data, "utf-8");
			System.out.println("我是客户端，服务端说："+request);
		} finally {
			//clent端读数据须手动释放msg，单如果要继续write，则不需要释放
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
