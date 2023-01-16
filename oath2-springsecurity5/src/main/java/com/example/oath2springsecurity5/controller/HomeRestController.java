package com.example.oath2springsecurity5.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class HomeRestController {

    /* OAuth2AuthenticationToken chính là principal của OAuth2 */
    @GetMapping("/user")
    public String user(HttpSession httpSession) {
//        httpSession.invalidate();
        return "Hello ! this is User Page ";
    }

    @GetMapping("/admin")
    public String admin(HttpSession httpSession) {
//        httpSession.invalidate();
        return "Hello ! this is Admin Page ";
    }

}
