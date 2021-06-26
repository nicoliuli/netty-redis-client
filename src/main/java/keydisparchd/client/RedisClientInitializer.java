package keydisparchd.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import keydisparchd.server.Server;

public class RedisClientInitializer extends ChannelInitializer<Channel> {


    private Server server;

    public RedisClientInitializer(Server server) {
        this.server = server;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ch.pipeline().addLast(new RedisClientHandler(server));

    }
}
