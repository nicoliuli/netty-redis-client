package keydisparchd.config;


import keydisparchd.model.RedisServerConfig;

import java.io.IOException;
import java.util.*;

public class ConfigUtil {
    public static Config config = new Config();

    static {
        Map<String, String> propMap = new HashMap<>();
        java.util.Properties prop = new java.util.Properties();
        try {
            prop.load(ConfigUtil.class.getClassLoader().getResourceAsStream("application.properties"));
            Set<String> keys = prop.stringPropertyNames();
            for (String key : keys) {
                String value = null;
                value = prop.getProperty(key);
                propMap.put(key, value);
            }

            config.setClientPort(Integer.parseInt(propMap.get("clientPort")));
            String redisServerList = propMap.get("redisServerList");
            String[] redisServer = redisServerList.split(",");
            List<RedisServerConfig> list = new ArrayList<>();
            for (String ipPort : redisServer) {
                String[] split = ipPort.split(":");
                RedisServerConfig redisServerConfig = new RedisServerConfig(split[0],Integer.parseInt(split[1]));
                list.add(redisServerConfig);
            }
            config.setRedisServerConfigList(list);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
