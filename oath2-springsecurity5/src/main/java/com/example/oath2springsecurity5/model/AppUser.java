package com.example.oath2springsecurity5.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    private int id;
    private String username;
    private String password;
    private String role;
}
