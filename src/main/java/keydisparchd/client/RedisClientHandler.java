package keydisparchd.client;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import keydisparchd.common.Constants;
import keydisparchd.server.Server;

public class RedisClientHandler extends ChannelDuplexHandler {


    private Server server;


    public RedisClientHandler(Server server) {
        this.server = server;
    }

    /**
     * 将redis的响应发送给redis-cli
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.writeAndFlush(msg);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 发给server,把client的channel通过attr属性带过去
        server.channel().attr(Constants.attributeKey).set(ctx.channel());
        server.channel().writeAndFlush(msg);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 这里如果是jedis关闭连接后，会抛异常
        // System.err.print("exceptionCaught: ");
        // cause.printStackTrace(System.err);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connection client active:" + ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connention client inactive:" + ctx.channel().id());
    }
}
