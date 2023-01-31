package com.example.apigateway.app.service;

import com.example.apigateway.app.dto.LoginRequest;
import com.example.apigateway.app.dto.LoginResponse;
import com.example.apigateway.common.oauth2_keycloak.GrantType;
import com.example.apigateway.common.oauth2_keycloak.KeycloakParams;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-id}")
    private String clientId;

//    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-secret}")
//    private String clientSecret;

    /* using username password to get access-token from keycloak */
    @Override
    public LoginResponse handleLogin(LoginRequest loginRequest) {

        HttpHeaders headers = new HttpHeaders();
        /* using application/x-www-form-urlencoded to send post request */
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(KeycloakParams.CLIENT_ID, clientId);
        /* client_secret only use with grant_type = client_credentials */
        map.add(KeycloakParams.CLIENT_SECRET, "");
        map.add(KeycloakParams.GRANT_TYPE, GrantType.PASSWORD);
        map.add(KeycloakParams.USERNAME, loginRequest.getUsername());
        map.add(KeycloakParams.PASSWORD, loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(tokenUrl, httpEntity, LoginResponse.class);
        return response.getBody();
    }
}
