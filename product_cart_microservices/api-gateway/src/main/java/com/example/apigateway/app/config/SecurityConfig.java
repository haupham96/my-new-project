package com.example.apigateway.app.config;

import com.example.apigateway.common.ReactiveWebFilter;
import com.example.apigateway.common.oauth2_keycloak.KeycloakScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {

        httpSecurity.cors().and().csrf().disable();
        httpSecurity.authorizeExchange(author ->
                        author
                                /* matcher để lấy token */
                                .pathMatchers("/api/auth/login").permitAll()

                                .pathMatchers("/api/cart/**").permitAll()
                                .pathMatchers("/api/pay/**").permitAll()
                                .pathMatchers("/api/promotion/**").permitAll()

                                .pathMatchers(HttpMethod.GET, "/api/product").permitAll()
                                .pathMatchers(HttpMethod.GET, "/api/product/{id}").permitAll()
                                .pathMatchers(HttpMethod.GET, "/api/product/name/**").permitAll()
                                .pathMatchers(HttpMethod.GET, "/eureka/web").permitAll()
                                .pathMatchers(HttpMethod.GET, "/eureka/**").permitAll()

                                /* authorizaiton by scope */
                                .pathMatchers(HttpMethod.POST, "/api/product").hasAuthority(KeycloakScope.UPDATE)
                                .pathMatchers(HttpMethod.PUT, "/api/product/**").hasAuthority(KeycloakScope.UPDATE)
                                .pathMatchers(HttpMethod.DELETE, "/api/product/**").hasAuthority(KeycloakScope.UPDATE)

                                .anyExchange().authenticated()
                )
                .addFilterBefore(new ReactiveWebFilter(), SecurityWebFiltersOrder.FIRST)
                .oauth2ResourceServer()
                .jwt();

        return httpSecurity.build();
    }

}
