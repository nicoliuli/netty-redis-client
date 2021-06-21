package cluster.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RedisClientHandler extends ChannelDuplexHandler {
    // 发送 redis 命令
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String aMsg = (String) msg;
        // 处理回车
        if (StringUtil.isNullOrEmpty(aMsg)) {
            return;
        }
        String[] commands = aMsg.split("\\s+");
        List<RedisMessage> children = new ArrayList<>(commands.length);
        for (String cmdString : commands) {
            // 以 $ 开头的多行字符串
            children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmdString)));
        }
        RedisMessage request = new ArrayRedisMessage(children);
        ctx.write(request, promise);
    }


    // 接收 redis 响应数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RedisMessage redisMessage = (RedisMessage) msg;
        // 打印响应消息
        printRedisResponse(redisMessage);
        // 是否资源
        ReferenceCountUtil.release(redisMessage);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.print("exceptionCaught: ");
        cause.printStackTrace(System.err);
        ctx.close();
    }


    private static void printRedisResponse(RedisMessage msg) {
        System.out.println(msg);
        if (msg instanceof SimpleStringRedisMessage) {
            // 以 + 开头的单行字符串
            System.out.println(((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            // 以 - 开头的错误信息
            System.out.println("(error) " + ((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            // 以 : 开头的整型数据
            System.out.println(((IntegerRedisMessage) msg).value());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            // 以 $ 开头的多行字符串
            System.out.println(getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            // 以 * 开头的数组
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                printRedisResponse(child);
            }
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }

    private static String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return "(nil)";
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }
}
