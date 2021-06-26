package keydisparchd.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import keydisparchd.common.Constants;

public class RedisServerHandler extends ChannelDuplexHandler {


    /**
     * 将redis-cli的数据发给redis
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.writeAndFlush(msg);
    }


    /**
     * 接收redis的响应，并发送给redis-cli
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("redis response is : "+msg);
        // 获取client的channel，把数据写回给redis-cli
        Channel cliantChannel = ctx.pipeline().channel().attr(Constants.attributeKey).get();
        cliantChannel.writeAndFlush(msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.print("exceptionCaught: ");
        cause.printStackTrace(System.err);
        ctx.close();
    }
}
