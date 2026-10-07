package com.miniredis.gateway.services;

import com.miniredis.gateway.dto.request.SetValueRequest;
import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.exception.KeyException;
import com.miniredis.gateway.exception.ValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KeyServiceTest {

    private StringRedisTemplate redis;
    private ValueOperations<String, String> valueOps;
    private KeyService keyService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redis = mock(StringRedisTemplate.class);
        valueOps = (ValueOperations<String, String>) mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valueOps);
        keyService = new KeyService(redis);
    }

    @Test
    void getValue_existingKey_returnsValue() throws Exception {
        when(valueOps.get("testKey")).thenReturn("testVal");

        RedisResponse<String> res = keyService.getValue("testKey");
        assertEquals("testVal", res.result());
    }

    @Test
    void getValue_nullKey_throwsKeyException() {
        assertThrows(KeyException.class, () -> keyService.getValue(null));
    }

    @Test
    void setKeyValue_validRequest_returnsOk() throws Exception {
        SetValueRequest req = new SetValueRequest("myKey", "myVal");

        RedisResponse<String> res = keyService.setKeyValue(req);
        assertEquals("OK", res.result());
        verify(valueOps).set("myKey", "myVal");
    }

    @Test
    void setKeyValue_nullKey_throwsKeyException() {
        assertThrows(KeyException.class, () -> keyService.setKeyValue(new SetValueRequest(null, "val")));
    }

    @Test
    void setKeyValue_nullValue_throwsValueException() {
        assertThrows(ValueException.class, () -> keyService.setKeyValue(new SetValueRequest("key", null)));
    }

    @Test
    void delValue_existingKey_returnsOne() throws Exception {
        when(redis.delete("delKey")).thenReturn(true);

        RedisResponse<Integer> res = keyService.delValue("delKey");
        assertEquals(1, res.result());
    }

    @Test
    void exists_existingKey_returnsOne() throws Exception {
        when(redis.hasKey("existKey")).thenReturn(true);

        RedisResponse<Integer> res = keyService.exists("existKey");
        assertEquals(1, res.result());
    }
}
