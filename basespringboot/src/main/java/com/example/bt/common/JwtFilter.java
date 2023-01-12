
package com.example.bt.common;

import com.example.bt.app.entity.AppUser;
import com.example.bt.app.service.user.IAppUserService;
import com.example.bt.common.SecurityJWT;
import com.example.bt.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : HauPV
 * Class dùng để kiểm tra Token cho mỗi request gửi đến server
 */
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IAppUserService iAppUserService;

    @Autowired
    ObjectMapper objectMapper;

    /*
     *  Mỗi khi có request đến sẽ lấy ra JWT trong request header Authorization
     * Sau đó sẽ kiểm tra và xác thực token xem có hơp lệ không
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Class - JwtFilter");
        log.info("method : doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)");

        String token = request.getHeader(SecurityJWT.AUTHORIZATION);
        if (token != null && token.startsWith(SecurityJWT.BEARER)) {
//        Nếu request header có key là Authorization và value bắt đầu bằng Bearer sẽ lấy token ra và xác thực
            try {
//              Bỏ chữ Bearer và phần còn lại là token cần sử dụng
                token = token.substring(7);
                log.info(token);
                Claims claims = jwtUtils.parseToken(token);

//              Nếu Token hợp lệ : lấy thông tin trong token và tìm user -> lưu vào SecurityContext
                AppUser user = iAppUserService.findByUsername(claims.get(SecurityJWT.USERNAME, String.class));
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));
                UserDetails userDetails = new User(user.getUsername(), user.getPassword(), authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception ex) {
                log.error("Invalid Token from filter : {} ", ex.getMessage());
                Map<String, String> err = new HashMap<>();
                err.put("error-token : ", ex.getMessage());
                objectMapper.writeValue(response.getOutputStream(), err);
            }
        }
        filterChain.doFilter(request, response);
        log.info("Kết thúc method : doFilterInternal()");
    }

}
