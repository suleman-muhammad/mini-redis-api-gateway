package com.miniredis.gateway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.exception.KeyException;

@Service 
public class IncrService {
    
    @Autowired 
    private final StringRedisTemplate redis;
    
    public IncrService(StringRedisTemplate redis){
        this.redis = redis;
    }

    public RedisResponse<Long> incr(String key) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        Long value = redis.opsForValue().increment(key);

        return new RedisResponse<>(value);
    }

    public RedisResponse<Long> incrBy(String key, long delta) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        Long value = redis.opsForValue().increment(key,delta);

        return new RedisResponse<>(value);
    }

}
