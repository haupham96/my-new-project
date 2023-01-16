package com.example.oath2springsecurity5.dto.response;

import com.example.oath2springsecurity5.dto.oauth2.OAuth2UserImpl;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer ";
    private OAuth2UserImpl user;

    public LoginResponse(String token, OAuth2UserImpl user) {
        this.token = token;
        this.user = user;
    }
}
