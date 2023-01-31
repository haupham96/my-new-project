package com.example.apigateway.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUser {
    /**
     * claims name base on keycloak jwt return
     * */
    /* claim preferred_username */
    private String username;
    /* claim name */
    private String fullName;
    /* claim email */
    private String email;
}
