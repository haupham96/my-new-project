package com.example.apigateway.app.controller;

import com.example.apigateway.app.dto.JwtUser;
import com.example.apigateway.app.dto.LoginRequest;
import com.example.apigateway.app.dto.LoginResponse;
import com.example.apigateway.app.service.ILoginService;
import com.example.apigateway.common.oauth2_keycloak.JwtClaim;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginRestController {
    private final ILoginService iLoginService;

    /* using keycloak user to get accessToken */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return iLoginService.handleLogin(loginRequest);
    }

    /* get principal info after authenticated */
    @GetMapping("/user")
    public JwtUser getAuthenticateUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString(JwtClaim.USERNAME);
        String fullName = jwt.getClaimAsString(JwtClaim.FULL_NAME);
        String email = jwt.getClaimAsString(JwtClaim.EMAIL);
        return JwtUser.builder()
                .email(email)
                .username(username)
                .fullName(fullName)
                .build();
    }

}
