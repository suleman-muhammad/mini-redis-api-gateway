package com.miniredis.gateway.controller;

import com.miniredis.gateway.dto.request.SetValueRequest;
import com.miniredis.gateway.dto.response.RedisResponse;
import com.miniredis.gateway.services.KeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KeyController.class)
public class KeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KeyService keyService;

    @Test
    void getValue_existingKey_returnsValueWithHttp200() throws Exception {
        when(keyService.getValue("mykey")).thenReturn(new RedisResponse<>("myvalue"));

        mockMvc.perform(get("/api/keys/mykey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("myvalue"));
    }

    @Test
    void setKeyValue_validRequest_returnsOkWithHttp200() throws Exception {
        when(keyService.setKeyValue(any(SetValueRequest.class))).thenReturn(new RedisResponse<>("OK"));

        mockMvc.perform(post("/api/keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"mykey\",\"value\":\"myvalue\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("OK"));
    }

    @Test
    void delValue_existingKey_returnsCountWithHttp200() throws Exception {
        when(keyService.delValue("mykey")).thenReturn(new RedisResponse<>(1));

        mockMvc.perform(delete("/api/keys/mykey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1));
    }

    @Test
    void keyExists_existingKey_returnsOneWithHttp200() throws Exception {
        when(keyService.exists("mykey")).thenReturn(new RedisResponse<>(1));

        mockMvc.perform(get("/api/keys/mykey/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1));
    }
}
