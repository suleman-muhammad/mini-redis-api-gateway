package com.miniredis.gateway.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class HealthController {
    @Autowired 
    private final StringRedisTemplate redis;
    
    public HealthController(StringRedisTemplate redis){
        this.redis = redis;
    }

    @GetMapping("/")
    public Map<String,String> getHealth(){
        try{
            String res = redis.getConnectionFactory().getConnection().ping();
            return Map.of(
                    "status","Up",
                    "redis",res);

        }catch (Exception e){
            return Map.of(
                    "status","Down",
                    "redis","DISCONNECTED");
        }
    }

}
