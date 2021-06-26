package test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisFactory {
    private static Jedis jedis = null;
    private static JedisPool pool = null;

    public static void connection() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(30);
        config.setMaxIdle(0);
        config.setMaxWaitMillis(10000);

        pool = new JedisPool(config, "127.0.0.1", 6380);
    }

    /**
     * 获取jedis
     *
     * @return
     * @throws Exception
     */
    public static Jedis getJedis() throws Exception {
        if (pool == null) {
            throw new Exception("get Jedis fail");
        }
        return pool.getResource();
    }

    public static void close(Jedis jedis){
        if(jedis != null){
       //     jedis.close();
            jedis.disconnect();
        }
    }

    public static void disConnection() {
        if (pool != null) {
            pool.close();
        }
        System.out.println("close redis ok");
    }


}
