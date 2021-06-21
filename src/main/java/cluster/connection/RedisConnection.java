package cluster.connection;

import cluster.handler.RedisClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RedisConnection {
    private String host;
    private int port;
    // 集群的标识
    private String node;
    private Channel channel;
    private static RedisConnectionPool pool;

    public RedisConnection(String host, int port) {
        this.host = host;
        this.port = port;
        node = host + "_" + port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new RedisClientInitializer());

            ChannelFuture f = bootstrap.connect(host, port).sync();
            channel = f.channel();
            pool.add(this.node, this);
            print(" Welcome to the Redis Server, host : " + host + ", port : " + port + ".");
            print(" Command end with \\r,command with 'quit or exit' to shutdown ");
            /*BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
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

            for (int i = 0; i < 2; i++) {
                channel.writeAndFlush("get b");
            }*/
            /*channel.close().sync().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println(" bye! ");
                }
            });*/
        } finally {
            //   group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        // -Dport=6379 -Dhost=127.0.0.1
        String host = System.getProperty("host");
        Integer port = Integer.parseInt(System.getProperty("port"));
        RedisConnection connection = new RedisConnection(host, port);
        connection.start();

        RedisConnection connection1 = new RedisConnection(host, port);
        connection1.start();

        RedisConnection connection2 = new RedisConnection(host, port);
        connection2.start();

    }

    private void print(String str) {
        System.out.println(str);
    }
}
