
package com.example.bt.app.dto.login;

import java.io.Serializable;

import com.example.bt.common.SecurityJWT;
import lombok.*;

/**
 * @author : HauPV
 * class LoginResponse trả về thông tin người dùng khi đăng nhập thành công
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginResponse implements Serializable {
    private String token;
    private String username;
    private String role;
    private String type = SecurityJWT.BEARER;

    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public LoginResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
