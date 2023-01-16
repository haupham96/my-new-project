package com.example.oath2springsecurity5.config;

import com.example.oath2springsecurity5.common.JwtFilter;
import com.example.oath2springsecurity5.common.JwtUtils;
import com.example.oath2springsecurity5.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppConfig {

    @Autowired
    UserServiceImpl userServiceImpl;

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
        authBuilder.userDetailsService(userServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeRequests()
                .antMatchers("/api/login/**").permitAll()
                .antMatchers("/oauth2/authorization/google").permitAll()
                .antMatchers("/oauth2/authorization/facebook").permitAll()
                .antMatchers("/user").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .antMatchers("/admin").hasAuthority("ROLE_ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage("/api/login/unauthenticated")
                .defaultSuccessUrl("/api/login/success", true)
                .failureUrl("/api/login/failure")
//                .authorizationEndpoint()
//                .authorizationRequestResolver(new CustomAuthorizationRequestResolver(this.clientRegistrationRepository))
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
//                .and()
//                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
//                .authorizationEndpoint()
//                .baseUri("/oauth2-authorize-client")
//                .authorizationRequestRepository(authorizationRequestRepository())
//                .and()
//                .tokenEndpoint()
//                .accessTokenResponseClient(accessTokenResponseClient())
//                .and()
//                .redirectionEndpoint()
//                .baseUri("/oauth2/redirect")
        ;

        return http.build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

//    @Bean
//    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
//    accessTokenResponseClient() {
//        return new NimbusAuthorizationCodeTokenResponseClient();
//    }
//
//    @Bean
//    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
//        return new HttpSessionOAuth2AuthorizationRequestRepository();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(this.webClient());
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtils(), passwordEncoder(), objectMapper());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
