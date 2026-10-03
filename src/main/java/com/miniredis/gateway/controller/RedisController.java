package com.miniredis.gateway.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.miniredis.gateway.dto.request.SetRequest;
import com.miniredis.gateway.dto.response.KeyValueResponse;
import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.services.RedisService;

@RestController 
public class RedisController {
    
    @Autowired 
    private final RedisService service;

    public RedisController(RedisService service){
        this.service = service;
    }

    @GetMapping("/api/keys/{key}")
    public ResponseEntity<?> getValue(@PathVariable String key){
        try{
            RedisResponse<String> res = service.getValue(key);
            return ResponseEntity.status(200).body(res);
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(Map.of("error","Internal Server Error."));
        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
        }
    }
}
