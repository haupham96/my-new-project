
package com.example.bt.app.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

/**
 * @author : HauPV
 * class LoginRequest nhận thông tin username , password từ client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Không được chứa kí tự dặc biệt .")
    private String username;
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Không được chứa kí tự dặc biệt .")
    private String password;
}
