
package com.example.bt.app.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.bt.app.dto.login.LoginRequest;
import com.example.bt.app.dto.login.LoginResponse;
import com.example.bt.app.service.jwt.IJwtService;

import lombok.RequiredArgsConstructor;

/**
 * @author : HauPV
 * API xác thực sử dụng JWT
 */
@Slf4j
@CrossOrigin(origins = {"http://localhost:8080"})
@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtRestController {

    private final IJwtService iJwtService;

    /*
    API Nhận login request là username và password và tiến hành xác thực .
    Nếu thành công sẽ trả về thông tin user và Token
    */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("class - JwtRestController");
        log.info("method :  login()");
        LoginResponse response = iJwtService.authenticate(loginRequest);
        return ResponseEntity.ok(response);

    }

}
