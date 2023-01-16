package com.example.oath2springsecurity5.service;

import com.example.oath2springsecurity5.Provider;
import com.example.oath2springsecurity5.common.JwtUtils;
import com.example.oath2springsecurity5.dto.oauth2.OAuth2UserImpl;
import com.example.oath2springsecurity5.dto.request.LoginRequest;
import com.example.oath2springsecurity5.dto.response.LoginResponse;
import com.example.oath2springsecurity5.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private final String pass123 = "$2a$10$DdQdyccwCVJYgfp3mcZ1D.2kxsx8Vl7PFGV/CGwrzO4rblds/veQW";
    private final AppUser admin = new AppUser(1, "admin", pass123, "ROLE_ADMIN");
    private final AppUser user = new AppUser(2, "user", pass123, "ROLE_USER");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals("admin")) {
            AppUser appUser = admin;
            return new User(
                    appUser.getUsername(), appUser.getPassword(),
                    true, true, true, true,
                    List.of(new SimpleGrantedAuthority(appUser.getRole()))
            );
        } else if (username.equals("user")) {
            AppUser appUser = user;
            return new User(
                    appUser.getUsername(), appUser.getPassword(),
                    true, true, true, true,
                    List.of(new SimpleGrantedAuthority(appUser.getRole()))
            );
        } else {
            throw new UsernameNotFoundException("notfound user : " + username);
        }
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Map<String, Object> attributes = new HashMap<>();
        if ("admin".equals(loginRequest.getUsername())) {
            AppUser appUser = admin;
            if (passwordEncoder.matches(loginRequest.getPassword(), appUser.getPassword())) {
                String token = jwtUtils.createToken(appUser.getUsername(), appUser.getRole());
                attributes.put("provider", Provider.SYSTEM.getProviderName());
                OAuth2UserImpl oAuth2User = new OAuth2UserImpl(appUser.getUsername(),
                        attributes,
                        List.of(new SimpleGrantedAuthority(appUser.getRole())));
                return new LoginResponse(token, oAuth2User);
            } else {
                throw new BadCredentialsException("invalid Password");
            }
        } else if ("user".equals(loginRequest.getUsername())) {
            AppUser appUser = user;
            if (passwordEncoder.matches(loginRequest.getPassword(), appUser.getPassword())) {
                String token = jwtUtils.createToken(appUser.getUsername(), appUser.getRole());
                OAuth2UserImpl oAuth2User = new OAuth2UserImpl(appUser.getUsername(),
                        attributes,
                        List.of(new SimpleGrantedAuthority(appUser.getRole())));
                return new LoginResponse(token, oAuth2User);
            } else {
                throw new BadCredentialsException("invalid Password");
            }
        } else {
            throw new UsernameNotFoundException("notfound username : " + loginRequest.getUsername());
        }
    }
}
