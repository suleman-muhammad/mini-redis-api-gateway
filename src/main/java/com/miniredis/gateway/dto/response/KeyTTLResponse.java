package com.miniredis.gateway.dto.response;

public record KeyTTLResponse(String key, long ttl) {
    
}
