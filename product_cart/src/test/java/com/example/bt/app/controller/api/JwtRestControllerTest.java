package com.example.bt.app.controller.api;

import com.example.bt.app.dto.login.LoginRequest;
import com.example.bt.app.dto.login.LoginResponse;
import com.example.bt.app.service.jwt.IJwtService;
import com.example.bt.common.SecurityJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author HauPV
 * Junit Test cho JwtRestController
 * => Done
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class JwtRestControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IJwtService iJwtService;

    private String baseUrl = "http://localhost:";
    private RestTemplate rest;

    @BeforeEach
    public void setup() {
        baseUrl += port + "/api/jwt/login";
        rest = new RestTemplate();
    }

    /**
     * Trường hợp login thành công
     */
    @Test
    void loginWhenSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("123");

        LoginResponse loginResponse = rest.postForObject(baseUrl, loginRequest, LoginResponse.class);
        assertNotNull(loginResponse);
        assertEquals(loginResponse.getUsername(), loginRequest.getUsername());
        assertEquals(SecurityJWT.ROLE_USER, loginResponse.getRole());
    }

    /**
     * Trường hợp login không thành công -> sai password
     */
    @Test
    void loginFailWithWrongPassWord() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("123456");

        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("error"));
    }

    /**
     * Trường hợp login không thành công -> lỗi validate
     */
    @Test
    void loginFailWithValidate() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("use'r");
        loginRequest.setPassword("123/456");

        mockMvc.perform(MockMvcRequestBuilders.multipart(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("error"));
    }

}
