
package com.example.bt.utils;

import com.example.bt.app.entity.AppUser;
import com.example.bt.common.SecurityJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : HauPV
 * lớp util để tạo JWT và validate JWT
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    //    Tạo ra 1 chuỗi JWT
    public String createToken(AppUser appUser) {
        log.info(this.getClass().getSimpleName());
        log.info("method - createToken()");

        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityJWT.CLAIM_ROLE, appUser.getRole().getRoleName());
        claims.put(SecurityJWT.USERNAME, appUser.getUsername());
        claims.put(SecurityJWT.CLAIM_SUBJECT, "HauPV");
        long expiredIn2Hours = new Date().getTime() + (2 * 60 * 60 * 1000);
        claims.put(SecurityJWT.CLAIM_EXPIRED_TIME, new Date(expiredIn2Hours));

        log.info("kết thúc method - createToken()");
        return Jwts.builder()
                .setSubject(appUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    //    Mã hoá chuỗi JWT thành Claims
    public Claims parseToken(String token) throws Exception {
        log.info(this.getClass().getSimpleName());
        log.info("method - parseToken()");
        log.info("kết thúc method - parseToken()");
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //    Xác thực mã JWT
    public boolean checkValidToken(String token) {
        log.info(this.getClass().getSimpleName());
        log.info("method - checkValidToken()");
        try {
            log.info("Khối try");
            parseToken(token);
            log.info("Kết thúc khối try");
            log.info("kết thúc method - checkValidToken()");
            return true;
        } catch (Exception ex) {
            log.info("khối catch");
            log.error("Invalid Token : {} ", ex.getMessage());
            log.info("kết thúc khối catch");
            log.info("kết thúc method - checkValidToken() -> Xảy ra Exception");
            return false;
        }
    }
}
