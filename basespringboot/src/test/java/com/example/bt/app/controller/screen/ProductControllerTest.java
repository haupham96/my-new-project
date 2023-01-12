package com.example.bt.app.controller.screen;

import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.Product;
import com.example.bt.app.repository.IProductRepository;
import com.example.bt.app.service.product.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author HauPV
 * JUnit Test cho ProductController
 * => Done
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    IProductService iProductService;
    @Autowired
    IProductRepository iProductRepository;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Product productTest;

    /**
     * Xoá data dư thừa sau khi Test xong .
     */
    @AfterEach
    public void clearData() {
        String deleteName1 = "Nokia 1280";
        String deleteName2 = "Nokia1234";
        Product product1 = this.iProductRepository.findByName(deleteName1);
        Product product2 = this.iProductRepository.findByName(deleteName2);
        if (product1 != null) {
            this.iProductRepository.delete(product1);
        }
        if (product2 != null) {
            this.iProductRepository.delete(product2);
        }
        this.iProductRepository.delete(productTest);
    }

    @BeforeEach
    void setUpData() {
        productTest = new Product("test11", 1000000L, "test11");
        this.iProductRepository.saveAndFlush(productTest);
    }

    /**
     * Trường hợp db trả về list product thành công
     */
    @DisplayName("GET /product")
    @Test
    void listProduct() throws Exception {
        mockMvc.perform(get("/product"))
                .andExpect(status().isOk())
                .andExpect(view().name("/product/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", Matchers.hasSize(Matchers.greaterThan(1))))
                .andDo(print());
    }

    /**
     * Trường hợp admin truy cập thành công vào trang thêm mới sản phẩm
     */
    @DisplayName("GET /product/create")
    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void createPage() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("/product/create"))
                .andExpect(model().attributeExists("productDTO"))
                .andDo(print());
    }

    /**
     * Trường hợp truy cập thất bại vào trang thêm mới sản phẩm với quyền USER
     */
    @DisplayName("GET /product/create")
    @Test
    @WithMockUser(username = "user", password = "123", authorities = "USER")
    void createPage_WithoutAuthority() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * Trường hợp thêm sản phẩm thất bại với lỗi Validate
     */
    @DisplayName("POST /product/create")
    @Test
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void createProduct_FailWithValidateError() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Nokia 1280");
        productDTO.setPrice(500000L);
        productDTO.setDescription("Đồ cổ của Nokia");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "Nokia{}$");
        params.add("price", "0000");
        params.add("description", "");
        mockMvc.perform(post("/product/create")
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("productDTO", "name"))
                .andExpect(model().attributeHasFieldErrors("productDTO", "price"))
                .andExpect(model().attributeHasFieldErrors("productDTO", "description"))
                .andDo(print());
    }

    /**
     * Trường hợp Thêm sản phẩm thất bại do đã bị trùng tên trong db
     */
    @DisplayName("POST /product/create")
    @Test
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void createProduct_FailWithDuplicateName() throws Exception {
        Product product = new Product();
        product.setName("Nokia 1280");
        product.setPrice(1000000);
        product.setDescription("Đồ cổ của Nokia");
        iProductRepository.saveAndFlush(product);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "Nokia 1280");
        params.add("price", "1000000");
        params.add("description", "Đồ cổ của Nokia");
        mockMvc.perform(post("/product/create")
                        .params(params))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(model().attributeHasFieldErrors("productDTO", "name"));
    }

    /**
     * Trường hợp thêm mới sản phẩm thất bại với giá trị null
     */
    @DisplayName("POST /product/create")
    @Test
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void createProduct_FailWithNullValueInput() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", null);
        params.add("price", null);
        params.add("description", null);
        mockMvc.perform(post("/product/create")
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/product/create"))
                .andExpect(model().attributeHasFieldErrors("productDTO", "name", "price", "description"))
                .andDo(print());
    }

    /**
     * Trường hợp thêm mới sản phẩm thành công với quyền ADMIN
     */
    @DisplayName("POST /product/create")
    @Test
    @WithMockUser(username = "admin", password = "123", authorities = "ADMIN")
    void createProduct_Success() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "Nokia1234");
        params.add("price", "1000000");
        params.add("description", "Description for Nokia1234");
        mockMvc.perform(post("/product/create")
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/product/create"))
                .andExpect(model().attributeHasNoErrors("productDTO"))
                .andDo(print());
    }

    /**
     * Trường hợp xem chi tiết sản phẩm thất bại với id không tìm thấy
     */
    @DisplayName("GET / product/detail/{id}")
    @Test
    void productDetail_NotFoundProduct() throws Exception {
        mockMvc.perform(get("/product/detail/{id}", 0))
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

    /**
     * Trường hợp xem chi tiết sản phẩm thất bại với id string :  abc
     */
    @DisplayName("GET / product/detail/{id}")
    @Test
    void productDetailNotFoundProduct() throws Exception {
        mockMvc.perform(get("/product/detail/{id}", "abc"))
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

    /**
     * Trường hợp xem chi tiết sản phẩm thành công với id hợp lệ
     */
    @DisplayName("GET /product/detail/{id}")
    @Test
    void productDetail_FindSuccess() throws Exception {
        mockMvc.perform(get("/product/detail/{id}", 14))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(view().name("/product/detail"))
                .andDo(print());
    }

    /**
     * Trường hợp điều hướng đến trang edit sản phẩm thất bại -> ko có quyền Admin
     */
    @Test
    @DisplayName("GET /product/edit/23")
    @WithMockUser(username = "test", authorities = "USER")
    void editPage_FailWithForbidden() throws Exception {
        mockMvc.perform(get("/product/edit/{id}", 23))
                .andExpect(forwardedUrl("/403"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * Trường hợp điều hướng đến trang edit sản phẩm thành công
     */
    @Test
    @DisplayName("GET /product/edit/21")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void editPage_Success() throws Exception {
        mockMvc.perform(get("/product/edit/{id}", 21))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(status().isOk())
                .andExpect(view().name("/product/edit"))
                .andDo(print());
    }

    /**
     * Trường hợp edit sản phẩm thất bại
     * -> trùng tên sản phẩm : iPhone 11
     */
    @Test
    @DisplayName("POST /product/edit/23")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void editProduct_FailWithDuplicateName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "23");
        params.add("name", "iPhone 11");
        params.add("price", "1000000");
        params.add("description", "Description for iPhone 11");
        mockMvc.perform(post("/product/edit/{id}", 23)
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(model().attributeHasFieldErrors("productDTO", "name"))
                .andExpect(view().name("/product/edit"))
                .andDo(print());
    }

    /**
     * Trường hợp edit sản phẩm thất bại
     * -> lỗi validate
     */
    @Test
    @DisplayName("POST /product/edit/23")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void editProduct_FailWithValidate() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "23");
        params.add("name", "iPhone$$"); // kí tự đặc biệt
        params.add("price", "0"); // > 1,000,000 đ
        params.add("description", ""); // để trống
        mockMvc.perform(post("/product/edit/{id}", 23)
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(model().attributeHasFieldErrors("productDTO", "name", "price", "description"))
                .andExpect(view().name("/product/edit"))
                .andDo(print());
    }

    /**
     * Trường hợp edit sản phẩm thất bại
     * -> input value null
     */
    @Test
    @DisplayName("POST /product/edit/23")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void editProduct_FailWithNullValueInput() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "23");
        params.add("name", null);
        params.add("price", null);
        params.add("description", null);
        mockMvc.perform(post("/product/edit/{id}", 23)
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(model().attributeHasFieldErrors("productDTO", "name", "price", "description"))
                .andExpect(view().name("/product/edit"))
                .andDo(print());
    }

    /**
     * Trường hợp edit sản phẩm thất bại
     * -> id sản phẩm không tìm thấy
     */
    @Test
    @DisplayName("POST /product/edit/23")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void editProduct_FailWithInvalidProductId() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", "0");
        params.add("name", "test1");
        params.add("price", "1000000");
        params.add("description", "description cho test1");
        mockMvc.perform(post("/product/edit/{id}", 23)
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Trường hợp edit sản phẩm thành công
     */
    @Test
    @DisplayName("POST /product/edit/id")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void editProduct_Success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", String.valueOf(productTest.getId()));
        params.add("name", "test2");
        params.add("price", "2000000");
        params.add("description", "description cho test2");
        mockMvc.perform(post("/product/edit/{id}", productTest.getId())
                        .params(params)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product"))
                .andDo(print());
    }

    /**
     * Trường hợp delete sản phẩm thất bại
     * -> ko có quyền Admin
     */
    @Test
    @DisplayName("GET /product/delete/23")
    @WithMockUser(username = "test", authorities = "USER")
    void delete_FailWithNoPermission() throws Exception {
        mockMvc.perform(get("/product/delete/{id}", 23))
                .andExpect(forwardedUrl("/403"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * Trường hợp delete sản phẩm thất bại
     * -> id ko hợp lệ : 1000
     */
    @Test
    @DisplayName("GET /product/delete/1000")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void delete_FailWithInvalidProductId() throws Exception {
        mockMvc.perform(get("/product/delete/{id}", 1000))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Trường hợp delete sản phẩm thành công
     * -> id  : 23
     */
    @Test
    @DisplayName("GET /product/delete")
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void delete_Success() throws Exception {

        mockMvc.perform(get("/product/delete/{id}", productTest.getId()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product"))
                .andDo(print());
    }

}
