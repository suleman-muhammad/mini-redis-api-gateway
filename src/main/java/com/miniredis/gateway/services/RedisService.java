package com.miniredis.gateway.services;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.miniredis.gateway.dto.request.SetRequest;
import com.miniredis.gateway.dto.response.KeyTTLResponse;
import com.miniredis.gateway.dto.response.KeyValueResponse;
import com.miniredis.gateway.exception.KeyException;
import com.miniredis.gateway.exception.ValueException;

@Service 
public class RedisService {
    
    @Autowired 
    private final StringRedisTemplate redis;
    
    public RedisService(StringRedisTemplate redis){
        this.redis = redis;
    }
    
    public KeyValueResponse getValue(String key) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        String value = redis.opsForValue().get(key);

        if(value == null){
            throw new KeyException("No value Assigned to Key: " + key + ".");
        }
        return new KeyValueResponse(key, value);
    }  

    public void setKeyValue(SetRequest request) throws Exception{
        if(request.key() == null){
            throw new KeyException("Key is Null.");
        }

        if(request.value() == null){
            throw new ValueException("Value is Null.");
        }

        redis.opsForValue().set(request.key(),request.value());
    } 

    public boolean delValue(String key) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        boolean result = redis.delete(key);


        if(!result){
            throw new KeyException("No value Assigned to Key: " + key + ".");
        }
        
        return true;
    }

    public KeyTTLResponse getTtl(String key) throws Exception{
        if(key == null){
            throw new KeyException("Key is Null.");
        }

        long value = redis.getExpire(key,TimeUnit.SECONDS);

        return new KeyTTLResponse(key, value);
    }  
}
