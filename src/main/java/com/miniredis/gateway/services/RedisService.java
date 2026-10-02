package com.miniredis.gateway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.miniredis.gateway.dto.request.SetRequest;
import com.miniredis.gateway.dto.response.KeyResponse;
import com.miniredis.gateway.exception.KeyException;
import com.miniredis.gateway.exception.ValueException;

@Service 
public class RedisService {
    
    @Autowired 
    private final StringRedisTemplate redis;
    
    public RedisService(StringRedisTemplate redis){
        this.redis = redis;
    }

}
