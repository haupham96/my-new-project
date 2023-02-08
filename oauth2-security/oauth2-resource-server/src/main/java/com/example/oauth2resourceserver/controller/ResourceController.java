package com.example.oauth2resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/api/user")
    public String[] getUsers() {
        return new String[]{"admin", "user"};
    }

}
