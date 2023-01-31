package com.example.vnpayservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUser {
    /* claim preferred_username */
    private String username;
    /* claim name */
    private String fullName;
    /* claim email */
    private String email;
}
