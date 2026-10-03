package com.miniredis.gateway.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.exception.KeyException;

@Service
public class TTLService {
    
    @Autowired 
    private final StringRedisTemplate redis;
    
    public TTLService(StringRedisTemplate redis){
        this.redis = redis;
    }

    public RedisResponse<Long> getExpiry(String key) throws Exception{

        if(key == null){
            throw new KeyException("Key is Null.");
        }

        long value = redis.getExpire(key,TimeUnit.SECONDS);

        return new RedisResponse<>(value);
    }  

    public RedisResponse<Integer> setExpiry(String key, long ttl) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        Boolean result = redis.expire(key,Expiration.from(ttl,TimeUnit.SECONDS));

        return new RedisResponse<>(Boolean.TRUE.equals(result) ? 1 : 0);
    }

    public RedisResponse<Integer> persistKey(String key) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        Boolean result = redis.persist(key);

        return new RedisResponse<>(Boolean.TRUE.equals(result) ? 1 : 0);
    }
}
