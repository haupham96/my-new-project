package com.example.bt.common;

/**
 * @author : HauPV
 * danh sách các tên của JWT security
 */
public class SecurityJWT {

    private SecurityJWT() {
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String USERNAME = "username";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_SUBJECT = "sub";
    public static final String CLAIM_EXPIRED_TIME = "exp";
}
