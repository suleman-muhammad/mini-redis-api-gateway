package com.miniredis.gateway.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.miniredis.gateway.services.IncrService;
import com.miniredis.gateway.dto.response.RedisResponse;

@RestController 
public class IncrController {
    
    @Autowired 
    private final IncrService service;

    public IncrController(IncrService service){
        this.service = service;
    }

    @PostMapping("/api/keys/{key}/incr")
    public ResponseEntity<?> incrValue(@PathVariable String key){
        try{
            RedisResponse<Long> res = service.incr(key);
            return ResponseEntity.status(200).body(res);
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(Map.of("error","Internal Server Error."));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

    @PostMapping("/api/keys/{key}/incrby")
    public ResponseEntity<?> incrValue(@PathVariable String key, @RequestParam(value = "delta") long delta){
        try{
            RedisResponse<Long> res = service.incrBy(key,delta);
            return ResponseEntity.status(200).body(res);
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(Map.of("error","Internal Server Error."));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error",e.getMessage()));
        }
    }

}
