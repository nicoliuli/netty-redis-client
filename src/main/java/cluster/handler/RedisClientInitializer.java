package cluster.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;

public class RedisClientInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 将redis响应的协议解码
        pipeline.addLast(new RedisDecoder());
        pipeline.addLast(new RedisBulkStringAggregator());
        pipeline.addLast(new RedisArrayAggregator());

        // 将RedisMessage编码成redis协议发送出去
        pipeline.addLast(new RedisEncoder());
        pipeline.addLast(new RedisClientHandler());
    }
}

/*
    str-->RedisClientHandler.write-->RedisEncoder-->redis

    print<--RedisClientHandler.readChannel0<--RedisArrayAggregator<--RedisBulkStringAggregator<--RedisDecoder<--redis
 */