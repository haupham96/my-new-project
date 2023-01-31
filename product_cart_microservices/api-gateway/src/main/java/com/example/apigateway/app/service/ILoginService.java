package com.example.apigateway.app.service;

import com.example.apigateway.app.dto.LoginRequest;
import com.example.apigateway.app.dto.LoginResponse;

public interface ILoginService {
    LoginResponse handleLogin(LoginRequest loginRequest);
}
