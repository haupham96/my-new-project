
package com.example.bt.app.service.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.bt.app.dto.login.LoginRequest;
import com.example.bt.app.dto.login.LoginResponse;
import com.example.bt.app.entity.AppUser;
import com.example.bt.app.service.user.IAppUserService;
import com.example.bt.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : HauPV
 * service cho JWT
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements IJwtService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IAppUserService iAppUserService;

//    Xác thực người dùng đăng nhập và trả về thông tin đăng nhập + token
    @Override
    public LoginResponse authenticate(LoginRequest request) {
        log.info("class - CartServiceImpl");
        log.info("method - authenticate()");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser user = iAppUserService.findByUsername(request.getUsername());
        String token = jwtUtils.createToken(user);
        log.info("kết thúc method - authenticate()");
        return new LoginResponse(token, user.getUsername(), user.getRole().getRoleName());
    }

}
