package com.example.bt.app.controller.screen;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author HauV
 * JUnit Test cho HomeController
 * => Done
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    /**
     * Trường hợp điều hướng thành công đến trang sản phẩm cho tất cả người dùng
     */
    @Test
    @DisplayName("GET /")
    void redirectToProductPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/product"));
    }

    /**
     * Trường hợp truy cập thành công đến trang login cho tất cả người dùng
     */
    @Test
    @DisplayName("GET /login")
    void loginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(model().attributeExists("loginRequest"))
                .andExpect(view().name("login"));
    }

    /**
     * Trường hợp đăng nhập thành công với tài khoản admin
     */
    @Test
    @DisplayName("POST /login-request")
    void loginSuccess() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "admin");
        params.add("password", "123");

        mockMvc.perform(post("/login-request")
                        .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Trường hợp đăng nhập thất bại cho user không tồn tại trong hệ thống
     */
    @Test
    @DisplayName("POST /login-request")
    void loginFailWithInvalidUsernameOrPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "abc");
        params.add("password", "123");

        mockMvc.perform(post("/login-request")
                        .params(params))
                .andExpect(redirectedUrl("/login?error=true"))
                .andDo(print());
    }

    /**
     * Trường hợp truy cập thành công đến trang admin với quyền ADMIN
     */
    @Test
    @DisplayName("GET /admin")
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void adminPageAccessSuccess() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(view().name("admin"));
    }

    /**
     * Trường hợp user truy cập trang ADMIN -> lỗi 403
     */
    @Test
    @DisplayName("GET /admin")
    @WithMockUser(username = "user", password = "123", authorities = "USER")
    void adminPageAccessDeniedWith403() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    /**
     * Trường hợp truy cập thành công đến trang user sau khi đã xác thực thành công
     */
    @Test
    @DisplayName("GET /user")
    @WithMockUser(username = "user", password = "123", authorities = "USER")
    void userPageAccessSuccessWithUserRole() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(view().name("user"));
    }

    /**
     * Trường hợp Admin truy cập được vào trang có quyền USER
     */
    @Test
    @DisplayName("GET /user")
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void userPageAccessSuccessWithAdminRole() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(view().name("user"));
    }

    /**
     * Trường hợp người dùng chưa đăng nhập truy cập vào trang USER -> 403
     */
    @Test
    @DisplayName("GET /user")
    @WithAnonymousUser
    void userPageAccessDeniedWith403() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}
