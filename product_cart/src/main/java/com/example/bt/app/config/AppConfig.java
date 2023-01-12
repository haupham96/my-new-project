
package com.example.bt.app.config;

import com.example.bt.app.repository.IAppUserRepository;
import com.example.bt.common.AdminInterceptor;
import com.example.bt.common.SecurityJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : HauPV
 * Class config cho chức năng xác thực và phân quyền và tạo bean ứng dụng
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableJpaAuditing
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private IAppUserRepository iAppUserRepository;

    /* xử lý interceptor để xác thực lại quyền admin */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.adminInterceptor())
                .excludePathPatterns("/product")
                .excludePathPatterns("/product/detail/**")
                .addPathPatterns(
                        "/product/**",
                        "/csv/**"
                );
    }

    private final UserDetailsService userDetailsService;

    //    Tạo bean SecurityFilterChain để phân quyền và xác thực trong hệ thống
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info(this.getClass().getSimpleName());
        log.info("method : securityFilterChain()");
        final String LOGIN_MATCHER = "/login";
        http
                .cors()
                .and()
                .csrf().disable();
//  Những trang cho phép truy cập tự do không cần xác thực
        http.authorizeHttpRequests(requests -> requests
                        .antMatchers("/").permitAll()
                        .antMatchers("/cart/**").permitAll()
                        .antMatchers("/image/**").permitAll()
                        .antMatchers("/static/**").permitAll()
                        .antMatchers("/css/**").permitAll()
                        .antMatchers("/js/**").permitAll()
                        .antMatchers("/api/jwt/login").permitAll()
                        .antMatchers("LOGIN_MATCHER").permitAll()
                        .antMatchers("/product").permitAll()
                        .antMatchers("/product/detail/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/pay/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/pay/**").permitAll()
                )
//                Xử lý form login được submit
                .formLogin(form -> form
                        .loginProcessingUrl("/login-request")
                        .loginPage(LOGIN_MATCHER)
                        .defaultSuccessUrl(LOGIN_MATCHER, true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                )
//                Xử lý khi đăng xuất
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("cart_id")
                        .logoutSuccessUrl(LOGIN_MATCHER)
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll());
//      Những trang yêu cầu có quyền mới được phép truy cập
        http.authorizeHttpRequests()
                .antMatchers("/test/**").hasAnyAuthority(SecurityJWT.ROLE_ADMIN, SecurityJWT.ROLE_USER)
                .antMatchers("/").hasAnyAuthority(SecurityJWT.ROLE_ADMIN, SecurityJWT.ROLE_USER)
                .antMatchers("/user").hasAnyAuthority(SecurityJWT.ROLE_ADMIN, SecurityJWT.ROLE_USER)
                .antMatchers("/api/jwt/user").hasAnyAuthority(SecurityJWT.ROLE_ADMIN, SecurityJWT.ROLE_USER)
                .antMatchers("/home").hasAnyAuthority(SecurityJWT.ROLE_ADMIN, SecurityJWT.ROLE_USER)
                .antMatchers("/admin").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .antMatchers("/api/jwt/admin").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .antMatchers("/product/create").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .antMatchers("/product/edit/**").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .antMatchers("/product/delete/**").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .antMatchers(HttpMethod.GET, "/csv/upload").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .antMatchers(HttpMethod.POST, "/csv/upload").hasAuthority(SecurityJWT.ROLE_ADMIN)
                .anyRequest()
                .authenticated();
//      Các trường hợp ko có quyền sẽ được controller có mapping /403 trong HomeController xử lý
        http.exceptionHandling().accessDeniedPage("/403");

        log.info("kết thúc method : securityFilterChain()");
        return http.build();
    }

    //    Khai báo sử dụng mã hoá mật khẩu bằng BCryptPasswordEncoder
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info(this.getClass().getSimpleName());
        log.info("method : configure(AuthenticationManagerBuilder auth)");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        log.info("Kết thúc method : configure()");
    }


    //    Tạo bean cho BCryptPasswordEncoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.info(this.getClass().getSimpleName());
        log.info("method : passwordEncoder()");
        return new BCryptPasswordEncoder();
    }

    //    Tạo bean cho AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        log.info("class - SecurityConfig");
        log.info("method : authenticationManagerBean(AuthenticationConfiguration configuration()");
        return configuration.getAuthenticationManager();
    }

    //    Tạo bean cho ObjectMapper -> sử dụng để viết các obj thành json
    @Bean
    public ObjectMapper objectMapper() {
        log.info("class - {}", this.getClass().getSimpleName());
        log.info("method : objectMapper()");
        return new ObjectMapper();
    }

    /* Bean của AdminInterceptor -> sd cho xác thực quyền admin cho các URI yêu cầu quyền admin */
    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor(this.iAppUserRepository);
    }
}
