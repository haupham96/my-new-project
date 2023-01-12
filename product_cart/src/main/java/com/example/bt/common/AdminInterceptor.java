package com.example.bt.common;

import com.example.bt.app.entity.AppUser;
import com.example.bt.app.repository.IAppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
/**
 * @author HauPV
 * interceptor xử lý xác thực cho quyền admin
 * */
@Slf4j
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final IAppUserRepository iAppUserRepository;

    /* Xác thực lại quyền admin trước khi vào controller */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : preHandle");
        Principal principal = request.getUserPrincipal();
        if (principal != null && principal.getName() != null) {
            /* Trường hợp user đã xác thực trong hệ thống */
            log.info("khối if : principal != null && principal.getName() != null");
            AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
            if (appUser != null && appUser.getRole() != null) {
                /* Trường hợp tìm thấy user trong db */
                log.info("khối if : appUser != null && appUser.getRole() != null");
                if (SecurityJWT.ROLE_ADMIN.equals(appUser.getRole().getRoleName())) {
                    /* role = ADMIN -> true */
                    log.info("khối if : SecurityJWT.ROLE_ADMIN.equals(appUser.getRole().getRoleName())");
                    log.info("kết thúc khối if : SecurityJWT.ROLE_ADMIN.equals(appUser.getRole().getRoleName())");
                    log.info("kết thúc khối if : appUser != null && appUser.getRole() != null");
                    log.info("kết thúc method : preHandle -> return true");
                    return true;
                } else {
                    /* Role != ADMIN -> false : set lại authentication cho user theo dữ liệu trong db */
                    log.info("khối else : SecurityJWT.ROLE_ADMIN.equals(appUser.getRole().getRoleName())");
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(appUser.getRole().getRoleName()));
                    UserDetails userDetails = new User(appUser.getUsername(), appUser.getPassword(), authorities);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("kết thúc khối if : appUser != null && appUser.getRole() != null");
                    log.info("kết thúc khối else : SecurityJWT.ROLE_ADMIN.equals(appUser.getRole().getRoleName())");
                    log.info("kết thúc method : preHandle -> redirect:/403");
                    response.sendRedirect("/403");
                }
            }
            log.info("kết thúc khối if : principal != null && principal.getName() != null");
        }
        log.info("kết thúc method : preHandle return fasle ");
        return false;
    }
}
