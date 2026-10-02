package com.miniredis.gateway.dto.response;

public record KeyExpiryResponse(String key, long ttl) {
    
}
