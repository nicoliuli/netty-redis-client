package keydisparchd.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import keydisparchd.server.Server;

import java.util.List;

public class RedisClientInitializer extends ChannelInitializer<Channel> {



    private List<Server> serverList;

    public RedisClientInitializer(List<Server> serverList) {
        this.serverList = serverList;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new RedisClientHandler(serverList));
    }
}
