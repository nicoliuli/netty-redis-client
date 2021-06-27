package keydisparchd;

import keydisparchd.client.Client;
import keydisparchd.config.ConfigUtil;
import keydisparchd.model.RedisServerConfig;
import keydisparchd.server.Server;

import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
    public static void main(String[] args) throws Exception {
        // 启动server,连接redis
        List<RedisServerConfig> redisServerConfigList = ConfigUtil.config.getRedisServerConfigList();
        List<Server> serverList = new ArrayList<>();
        for (RedisServerConfig redisServerConfig : redisServerConfigList) {
            Server aServer = new Server(redisServerConfig.getIp(),redisServerConfig.getPort());
            serverList.add(aServer);
            aServer.start();
        }

        // 启动client，连接netty-server，和redis-cli
        Client client = new Client(ConfigUtil.config.getClientPort(),serverList);
        client.start();
    }
}
