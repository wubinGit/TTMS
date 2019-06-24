package com.ttms.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: <br>
 * 〈〉  分布式锁
 * @Param:
 * @Return: 
 * @Author: 万少波
 * @Date: 2019/6/19 21:03
 */
@Component
public class DistributorLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean setIfAbsent(final String key, final Serializable value, final long exptime) {
        Boolean b = (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                Object obj = connection.execute("set", keySerializer.serialize(key),
                        valueSerializer.serialize(value),
                        SafeEncoder.encode("NX"),
                        SafeEncoder.encode("EX"),
                        Protocol.toByteArray(exptime));
                return obj != null;
            }
        });
        return b;
    }

    public boolean expireNow(final String key){
        return this.expire(key,0,TimeUnit.MILLISECONDS);
    }

    public boolean expire(final String key , Integer time , TimeUnit timeUnit){
        return redisTemplate.expire(key,time, timeUnit);
    }
}
