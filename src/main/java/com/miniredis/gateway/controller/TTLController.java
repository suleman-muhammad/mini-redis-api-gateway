package com.miniredis.gateway.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.miniredis.gateway.dto.request.SetTimeToLiveRequest;
import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.services.TTLService;

@RestController 
public class TTLController {
    
    @Autowired 
    private final TTLService service;

    public TTLController(TTLService service){
        this.service = service;
    }

    @GetMapping("/api/keys/{key}/ttl")
    public ResponseEntity<?> getTimeToLiveValue(@PathVariable String key){
        try{
            RedisResponse<Long> res = service.getExpiry(key);
            return ResponseEntity.status(200).body(res);
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(Map.of("error","Internal Server Error."));
        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
        }
    }

    @PostMapping("/api/keys/ttl")
    public ResponseEntity<?> setTimeToLiveValue(@RequestBody SetTimeToLiveRequest request ){
        try{
            RedisResponse<Integer> res = service.setExpiry(request.key(),request.ttl());
            return ResponseEntity.status(200).body(res);
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(Map.of("error","Internal Server Error."));
        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
        }
    }

    @PostMapping("/api/keys/{key}/persist")
    public ResponseEntity<?> persistKey(@PathVariable String key){
        try{
            RedisResponse<Integer> res = service.persistKey(key);
            return ResponseEntity.status(200).body(res);
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(Map.of("error","Internal Server Error."));
        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
        }
    }
}
