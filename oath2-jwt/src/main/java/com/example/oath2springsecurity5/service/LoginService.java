package com.example.oath2springsecurity5.service;

import com.example.oath2springsecurity5.dto.oauth2.OAuth2UserImpl;
import com.example.oath2springsecurity5.dto.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class LoginService {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public LoginResponse handleOAuth2LoginSuccess(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());
        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
            HttpEntity entity = new HttpEntity("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> attributes = (Map<String, Object>) response.getBody();
                attributes.put("provider",authentication.getAuthorizedClientRegistrationId());
                OAuth2UserImpl oAuth2User = new OAuth2UserImpl((String) attributes.get("email"),
                        attributes,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
                return new LoginResponse(client.getAccessToken().getTokenValue(), oAuth2User);
            }
        }
        throw new BadCredentialsException(" Login Failed ! ");
    }
}
