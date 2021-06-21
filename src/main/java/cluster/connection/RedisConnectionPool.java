package cluster.connection;

import java.util.concurrent.ConcurrentHashMap;

public class RedisConnectionPool {
    private static ConcurrentHashMap<String, RedisConnection> pool = new ConcurrentHashMap<>();

    public static void add(String node,RedisConnection connection){
        pool.put(node,connection);
    }

    public static void remove(String node){
        pool.remove(node);
    }
}
