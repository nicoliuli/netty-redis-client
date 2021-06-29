package keydisparchd.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import keydisparchd.config.ConfigUtil;

import java.util.ArrayList;
import java.util.Random;

public class Server {
    private String host;
    private int port;
    private Channel channel;
    private ArrayList<Channel> serverChannel = new ArrayList<>(ConfigUtil.config.getServerChannel());


    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new RedisServerInitializer());

            ChannelFuture f = bootstrap.connect(host, port).sync().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println("server start");
                }
            });
            channel = f.channel();
            serverChannel.add(channel);

        } finally {

        }
    }

    public Channel channel() {
        return channel;
    }

    public Channel getRandomChannel() {
        if (ConfigUtil.config.getServerChannel() == 1) {
            return serverChannel.get(0);
        }
        int idx = new Random().nextInt(ConfigUtil.config.getServerChannel());
        System.out.println("server channel idx = " + idx);
        return serverChannel.get(idx);
    }


}
