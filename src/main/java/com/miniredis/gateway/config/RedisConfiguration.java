package com.miniredis.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration 
public class RedisConfiguration {
    
    @Bean 
    public JedisConnectionFactory jedisConnectionFactory(@Value("${REDIS_HOST:localhost}") String host, @Value("${REDIS_PORT:6380}") int port){
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host,port);
        return new JedisConnectionFactory(config);
    }


    @Bean
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory factory){
        return  new StringRedisTemplate(factory);
    }

}
