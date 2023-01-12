package com.example.bt.app.controller.screen;

import com.example.bt.app.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author HauPV
 * JUnit Test cho CSVController
 * -> Run xong vào db xoá các sp import từ file csv
 * => Done
 */
@AutoConfigureMockMvc
@SpringBootTest
class CSVControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    private MultipartFile csv;

    /**
     * Khai báo trước các file CSV sẽ sử dụng cho các test case
     */
    @BeforeEach
    public void setUp() throws IOException {
        ClassPathResource resourceProducts = new ClassPathResource("/static/csv/products.csv");
        csv = new MockMultipartFile("products.csv", resourceProducts.getInputStream().readAllBytes());
    }

    /**
     * Test trường hợp diều hướng thành công đến trang upload csv -> với quyền Admin
     */
    @Test
    @DisplayName("GET /csv/upload")
    @WithMockUser(username = "admin", password = "123", authorities = {"ADMIN"})
    void uploadPageWithAdminToken() throws Exception {
        mockMvc.perform(get("/csv/upload")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * Test trường hợp điều đến trang upload thất bại -> quyền user -> lỗi 403
     */
    @Test
    @DisplayName("GET /csv/upload")
    @WithMockUser(username = "user", password = "123", authorities = {"USER"})
    void uploadPageFailWith403() throws Exception {
        mockMvc.perform(get("/csv/upload"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * Test trường hợp upload và thêm dữ liệu trong file csv thành công vào db .
     */
    @Test
    @DisplayName("POST /csv/upload")
    @WithMockUser(username = "admin", password = "123", authorities = {"ADMIN"})
    void uploadCsvFileSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/csv/upload")
                        .file("csv", csv.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(redirectedUrl("/product"))
                .andDo(print());
    }

    /**
     * Test trường hợp upload không thành công -> dùng quyền user -> lỗi 403
     */
    @Test
    @DisplayName("POST /csv/upload")
    @WithMockUser(username = "user", password = "123")
    void uploadCsvFileFailWith403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/csv/upload")
                        .file("csv", csv.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * Test trường hợp file csv empty
     */
    @Test
    @DisplayName("POST /csv/upload")
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void uploadCsvFileWithEmptyFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/csv/upload")
                        .file("csv", null)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name("/csv/upload-page"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

}
