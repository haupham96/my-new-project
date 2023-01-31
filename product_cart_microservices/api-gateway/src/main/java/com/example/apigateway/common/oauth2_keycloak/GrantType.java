package com.example.apigateway.common.oauth2_keycloak;

public interface GrantType {
    String CLIENT_CREDENTIALS = "client_credentials";
    String REFRESH_TOKEN = "refresh_token";
    String PASSWORD = "password";
}
