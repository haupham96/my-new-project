package com.example.oath2springsecurity5.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
