package keydisparchd.config;

import keydisparchd.model.RedisServerConfig;

import java.util.ArrayList;
import java.util.List;

public class Config {
    /**
     * client监听的端口
     */
    private Integer clientPort;

    /**
     * redis server的ip和端口
     */
    private List<RedisServerConfig> redisServerConfigList = new ArrayList<>();

    public Config() {
    }

    public Config(Integer clientPort, List<RedisServerConfig> redisServerConfigList) {
        this.clientPort = clientPort;
        this.redisServerConfigList = redisServerConfigList;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public List<RedisServerConfig> getRedisServerConfigList() {
        return redisServerConfigList;
    }

    public void setRedisServerConfigList(List<RedisServerConfig> redisServerConfigList) {
        this.redisServerConfigList = redisServerConfigList;
    }
}
