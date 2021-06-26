package test;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class JedisTest {
    public static void main(String[] args) throws Exception {
        RedisFactory.connection();
        Jedis jedis = RedisFactory.getJedis();

        int i = 0;

        for(int j = 0;i<2;j++){
            String v = jedis.get("key"+new Random().nextInt(10000));
            System.out.println(v);
            System.out.println(jedis.get("a"));
            jedis.set("key"+i,i+"");
            i++;
        }



           Thread.sleep(999999999L);





    }
}
