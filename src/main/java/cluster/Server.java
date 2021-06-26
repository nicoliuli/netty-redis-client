package cluster;

import cluster.connection.RedisConnection;


public class Server {
    public static void main(String[] args) throws Exception{
        /********启动 netty和redis的链接********/
        // 读取配置文件

        // 创建链接，启动
        RedisConnection connection = new RedisConnection("127.0.0.1", 6379);
        connection.start();

        // 添加hook



    }
}
