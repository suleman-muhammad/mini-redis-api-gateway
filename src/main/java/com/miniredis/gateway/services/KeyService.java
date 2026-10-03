package com.miniredis.gateway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.miniredis.gateway.dto.request.SetValueRequest;
import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.exception.KeyException;
import com.miniredis.gateway.exception.ValueException;

@Service 
public class KeyService {
    
    @Autowired 
    private final StringRedisTemplate redis;
    
    public KeyService(StringRedisTemplate redis){
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

    public RedisResponse<Integer> exists(String key) throws Exception{

        if(key == null){
            throw new KeyException("Key is Null.");
        }

        Boolean result = redis.hasKey(key);
        
       return new RedisResponse<>(Boolean.TRUE.equals(result) ? 1 : 0);
    }

}
