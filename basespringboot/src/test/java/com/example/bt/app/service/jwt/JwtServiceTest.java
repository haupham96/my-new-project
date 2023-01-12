package com.example.bt.app.service.jwt;

import com.example.bt.app.dto.login.LoginRequest;
import com.example.bt.app.dto.login.LoginResponse;
import com.example.bt.common.SecurityJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author HauPV
 * JUnit Test cho JwtServiceImpl
 */
@SpringBootTest
class JwtServiceTest {

    @Autowired
    private IJwtService iJwtService;

    private LoginRequest userRequest;
    private LoginRequest adminRequest;

    /**
     * setUp trước 2 tài khoản hợp lệ trong hệ thống
     */
    @BeforeEach
    public void setUp() {
        userRequest = new LoginRequest("user", "123");
        adminRequest = new LoginRequest("admin", "123");
    }

    /**
     * Test cho trường hợp thông tin đăng nhập hợp lệ
     */
    @Test
    @DisplayName("authenticate_Success")
    void authenticate_Success() {
        LoginResponse userResponse = this.iJwtService.authenticate(userRequest);
        assertEquals("user", userResponse.getUsername());
        assertEquals(SecurityJWT.ROLE_USER, userResponse.getRole());

        LoginResponse adminResponse = this.iJwtService.authenticate(adminRequest);
        assertEquals("admin", adminResponse.getUsername());
        assertEquals(SecurityJWT.ROLE_ADMIN, adminResponse.getRole());
    }

    /**
     * Test cho trường hợp thông tin đăng nhập không hợp lệ
     * -> có kí tự đặc biệt
     */
    @Test
    @DisplayName("authenticate_FailWithSpecialCharacter")
    void authenticate_FailWithSpecialCharacter() {
        LoginRequest loginRequest = new LoginRequest("a'bc", "123");
        assertThrows(BadCredentialsException.class, () -> this.iJwtService.authenticate(loginRequest));
    }

    /**
     * Test cho trường hợp thông tin đăng nhập không hợp lệ
     * -> sai username
     */
    @Test
    @DisplayName("authenticate_FailWithInValidUser")
    void authenticate_FailWithInValidUser() {
        LoginRequest loginRequest = new LoginRequest("abc", "123");
        assertThrows(BadCredentialsException.class, () -> this.iJwtService.authenticate(loginRequest));
    }

    /**
     * Test cho trường hợp thông tin đăng nhập không hợp lệ
     * -> sai password
     */
    @Test
    @DisplayName("authenticate_FailWithInValidPassword")
    void authenticate_FailWithInValidPassword() {
        LoginRequest loginRequest = new LoginRequest("user", "123456");
        assertThrows(BadCredentialsException.class, () -> this.iJwtService.authenticate(loginRequest));
    }


}
