package com.example.oath2springsecurity5.common;

import com.example.oath2springsecurity5.Provider;
import com.example.oath2springsecurity5.dto.oauth2.OAuth2UserImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : HauPV
 * Class dùng để kiểm tra Token cho mỗi request gửi đến server
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter
        extends OncePerRequestFilter {


    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    /*
     *  Mỗi khi có request đến sẽ lấy ra JWT trong request header Authorization
     * Sau đó sẽ kiểm tra và xác thực token xem có hơp lệ không
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Class - JwtFilter");
        log.info("method : doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)");

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
//        Nếu request header có key là Authorization và value bắt đầu bằng Bearer sẽ lấy token ra và xác thực
            try {
//              Bỏ chữ Bearer và phần còn lại là token cần sử dụng
                token = token.substring(7);
                log.info(token);
                String providerName = request.getHeader("Provider");

                if (providerName == null || "".equals(providerName)){
                    throw new Exception("Not found HttpHeaders : Provider");
                }

                Provider provider = Provider.getProvider(providerName);

                if (provider == Provider.SYSTEM) {
                    OAuth2UserImpl oAuth2User = jwtUtils.parseTokenSystemJwt(token);
                    OAuth2AuthenticationToken oAuth2AuthenticationToken =
                            new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), providerName);
                    oAuth2AuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);
                } else if (provider == Provider.FACEBOOK || provider == Provider.GOOGLE) {
                    OAuth2UserImpl oAuth2User = jwtUtils.parseTokenOauth2(token, provider);
                    OAuth2AuthenticationToken authen = new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), providerName);
                    authen.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authen);
                }
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
