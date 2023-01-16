package com.example.oath2springsecurity5.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestToken {

    private String token;
    private String tokenType;
    private String providerName;

}
