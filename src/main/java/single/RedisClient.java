package single;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RedisClient {
    private String host;
    private int port;

    public RedisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new RedisClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            print(" Welcome to the Redis Server, host : " + host + ", port : " + port + ".");
            print(" Command end with \\r,command with 'quit or exit' to shutdown ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            ChannelFuture future = null;
            for (; ; ) {
            //    System.out.print(host + ":" + port + "> ");
                String s = in.readLine();
                if (s.equalsIgnoreCase("quit") || s.equalsIgnoreCase("exit")) {
                    break;
                }

                future = channel.writeAndFlush(s).sync();
                future.addListener(new GenericFutureListener<ChannelFuture>() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            System.err.print("write failed: ");
                            future.cause().printStackTrace(System.err);
                        }
                    }
                });
            }
            System.out.println(" bye! ");
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
       // -Dport=6379 -Dhost=127.0.0.1
        String host = System.getProperty("host");
        Integer port = Integer.parseInt(System.getProperty("port"));
        RedisClient client = new RedisClient(host, port);
        client.start();
    }

    private void print(String str) {
        System.out.println(str);
    }
}
