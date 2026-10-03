package com.miniredis.gateway.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import com.miniredis.gateway.dto.request.SetValueRequest;
import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.exception.KeyException;
import com.miniredis.gateway.exception.ValueException;

@Service 
public class RedisService {
    
    @Autowired 
    private final StringRedisTemplate redis;
    
    public RedisService(StringRedisTemplate redis){
        this.redis = redis;
    }
    
    public RedisResponse<String> getValue(String key) throws Exception{

        if(key == null){
            throw new KeyException("Key is Null.");
        }

        String value = redis.opsForValue().get(key);

        return new RedisResponse<String>(value);
    }  

    public RedisResponse<String> setKeyValue(SetValueRequest request) throws Exception{
        if(request.key() == null){
            throw new KeyException("Key is Null.");
        }

        if(request.value() == null){
            throw new ValueException("Value is Null.");
        }

        redis.opsForValue().set(request.key(),request.value());
        return new RedisResponse<String>("OK");
    } 

    public RedisResponse<Integer> delValue(String key) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        Boolean result = redis.delete(key);
        
        return new RedisResponse<>(Boolean.TRUE.equals(result) ? 1 : 0);
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
