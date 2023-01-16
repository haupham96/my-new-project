package com.example.oath2springsecurity5.common;

import com.example.oath2springsecurity5.Provider;
import com.example.oath2springsecurity5.dto.RequestToken;
import com.example.oath2springsecurity5.dto.oauth2.OAuth2UserImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${oauth2.google.extract-token-url}")
    private String googleExtractTokenUrl;
    @Value("${oauth2.facebook.extract-token-url}")
    private String facebookExtractTokenUrl;
    private final WebClient webClient;
    private final String secretKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    //    Tạo ra 1 chuỗi JWT
    public String createToken(String username, String role) {
        log.info(this.getClass().getSimpleName());
        log.info("method - createToken()");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("username", username);
        claims.put("sub", "HauPV");
        claims.put("iat", new Date(System.currentTimeMillis()));
        long expiredIn2Hours = new Date().getTime() + (2 * 60 * 60 * 1000);
        claims.put("exp", new Date(expiredIn2Hours));

        log.info("kết thúc method - createToken()");
        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    //    Mã hoá chuỗi JWT thành Claims
    public UsernamePasswordAuthenticationToken verifyToken(RequestToken requestToken) throws Exception {
        UsernamePasswordAuthenticationToken authenticate = null;
        Provider provider = Provider.getProvider(requestToken.getProviderName());
        if (provider == Provider.SYSTEM) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(requestToken.getToken())
                    .getBody();
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            authenticate = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority(role.toUpperCase())));
        } else if (provider == Provider.FACEBOOK || provider == Provider.GOOGLE) {
            String url = "facebook".equals(provider.getProviderName()) ? facebookExtractTokenUrl : googleExtractTokenUrl;
            ResponseEntity<?> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION,
                            requestToken.getTokenType() + " " + requestToken.getToken())
                    .retrieve()
                    .bodyToMono(ResponseEntity.class)
                    .block();
            assert response != null;
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, ?> responseBody = (Map<String, ?>) response.getBody();
                assert responseBody != null;
                String email = (String) responseBody.get("email");
                String name = (String) responseBody.get("name");

            }
        }
        return authenticate;

    }

    public OAuth2UserImpl parseTokenSystemJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("provider", Provider.SYSTEM);
//        attributes.put("system", "authorizaiton-server-jwt");
        return new OAuth2UserImpl(username, attributes, List.of(new SimpleGrantedAuthority(role)));
    }

    public OAuth2UserImpl parseTokenOauth2(String token, Provider provider) {

        String url = Provider.FACEBOOK.equals(provider) ? facebookExtractTokenUrl : googleExtractTokenUrl;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity entity = new HttpEntity("", headers);
        ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        assert response != null;
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> attributes = (Map<String, Object>) response.getBody();
            assert attributes != null;
            String email = (String) attributes.get("email");
            attributes.put("provider", provider);
            return new OAuth2UserImpl(email, attributes, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        } else {
            throw new BadCredentialsException("invalid Token");
        }

    }
}
