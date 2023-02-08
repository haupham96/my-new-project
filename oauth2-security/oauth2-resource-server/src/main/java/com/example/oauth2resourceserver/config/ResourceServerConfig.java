package com.example.oauth2resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .mvcMatchers("/api/**")
//                .access("hasAuthority('SCOPE_api.read')")
//                .and()
//                .oauth2ResourceServer()
//                .jwt();
//        return http.build();
        http
                .authorizeRequests()
                .mvcMatchers("/api/**")
                .access("hasAuthority('SCOPE_api.read')")
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

}
