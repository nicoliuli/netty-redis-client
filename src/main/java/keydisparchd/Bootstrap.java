package keydisparchd;

import keydisparchd.client.Client;
import keydisparchd.server.Server;

public class Bootstrap {
    public static void main(String[] args) throws Exception {
        // 启动server,连接redis
        Server server = new Server("127.0.0.1",6379);
        server.start();

        // 启动client，连接netty-server，和redis-cli
        Client client = new Client(6381);
        client.start(server);
    }
}
