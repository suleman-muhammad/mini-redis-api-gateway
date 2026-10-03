package com.miniredis.gateway.dto.request;

public record SetTimeToLiveRequest(String key, Long ttl) {
} 