package com.example.bt.app.controller.screen;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.service.cart.ICartService;
import com.example.bt.app.service.cart_product.ICartProductService;
import com.example.bt.app.service.product.IProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author HauPV
 * JUnit Test cho CartController
 * -> điều kiện cần  : User { test có cart_id = 193 }
 * => Done
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private Cookie cookie;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private ICartProductService iCartProductService;

    @Autowired
    private ICartService iCartService;

    /**
     * Tạo trước dữ liệu cartDTO mặc định cho các test case .
     */
    @BeforeEach
    public void setUp() {
        cookie = new Cookie("cart_id", "193");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 5); // 5 phút
    }

    /**
     * Test cho trường hợp request lấy thông tin giỏ hàng thành công
     * -> có thông tin trong db
     */
    @Test
    @WithMockUser(username = "test", authorities = "USER")
    @DisplayName("GET /cart")
    void cartDetails() throws Exception {
        mockMvc.perform(get("/cart").cookie(this.cookie))
                .andExpect(model().attributeExists("cart"))
                .andExpect(view().name("cart/cart"))
                .andDo(print());
    }

    /**
     * Test trường hợp cookie chứa id không có trong db : id = 0
     * -> Trả về giỏ hàng rỗng : new CartDTO() ;
     */
    @Test
    @DisplayName("GET /cart")
    void cartDetails_WithInValidCartId() throws Exception {
        Cookie cookiedefault = new Cookie("cart_id", "0");
        cookiedefault.setMaxAge(60 * 5); // 5 phút
        cookiedefault.setPath("/");

        mockMvc.perform(get("/cart").cookie(cookiedefault))
                .andExpect(model().attributeExists("cart"))
                .andExpect(view().name("cart/cart"))
                .andExpect(model().attribute("cart", new CartDTO()))
                .andDo(print());
    }

    /**
     * Test trường hợp thêm hàng vào giỏ thất bại
     * -> không tìm thấy sản phẩm theo product_id : 0
     */
    @Test
    @DisplayName("POST /cart/add-product/0")
    void addToCart_InvalidProductWithId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/add-product/{productId}", 0))
                .andExpect(MockMvcResultMatchers.view().name("error-page"))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Test trường hợp thêm hàng vào giỏ thất bại
     * -> product_id = ""
     */
    @Test
    @DisplayName("POST /cart/add-product/''")
    void addToCart_StringProductId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/cart/add-product/{productId}", "\"\""))
                .andExpect(MockMvcResultMatchers.view().name("error-page"))
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Test trường hợp thêm hàng vào giỏ thất bại
     * -> số lượng sản phẩm = 0 .
     */
    @Test
    @DisplayName("POST /cart/add-product/14")
    void addToCart_WithZeroQuantity() throws Exception {
        mockMvc.perform(post("/cart/add-product/{productId}", 14)
                        .param("quantity", "0"))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Test trường hợp thêm hàng vào giỏ thất bại
     * -> Không gửi số lượng sản phẩm muốn thêm .
     */
    @Test
    @DisplayName("POST /cart/add-product/14")
    void addToCartWithoutQuantity() throws Exception {
        mockMvc.perform(post("/cart/add-product/{productId}", 14))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Test cho trường hợp thêm mới sản phẩm vào giỏ hàng thành công
     * -> product_id có tồn tại trong db
     * PARAMS : cart_id = 193 , quantity = 2 , product_id = 21
     */
    @Test
    @WithMockUser(username = "test", authorities = "USER")
    @DisplayName("POST /cart/add-product/21")
    void addToCartSuccess() throws Exception {

        /* Set up db trước khi test */
        this.iCartProductService.removeAllCartProductByCartId(193);

        ProductDTO productDTO = this.iProductService.findById(21);
        Assertions.assertNotNull(productDTO);

        mockMvc.perform(post("/cart/add-product/{productId}", 21)
                        .param("quantity", "2").cookie(this.cookie))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("cart",
                        Matchers.hasProperty("cartId", Matchers.is(193))))
                .andExpect(model().attribute("cart",
                        Matchers.hasProperty("products", Matchers.aMapWithSize(1))))
                .andExpect(model().attribute("cart",
                        Matchers.hasProperty("products",
                                Matchers.hasEntry(Matchers.is(productDTO), Matchers.is(2)))))
                .andExpect(view().name("cart/cart"))
                .andDo(print());
    }

    /**
     * Test trường hợp xoá hàng trong giỏ thất bại
     * -> không tìm thấy sản phẩm theo id : 1000
     */
    @Test
    @DisplayName("GET /cart/delete-product/1000")
    void deleteFail() throws Exception {
        mockMvc.perform(get("/cart/delete-product/{productId}", 1000))
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

    /**
     * Test trường hợp xoá sản phẩm trong giỏ thành công
     * -> product_id : 14 , Cookie { cart_id : 193 } , User { test : ROLE_USER }
     */
    @Test
    @WithMockUser(username = "test", authorities = "USER")
    @DisplayName("GET /cart/delete-product/14")
    void deleteProductSucess() throws Exception {
        /* Thêm sp vào giỏ trc khi xoá */
        ProductDTO productDTO = this.iProductService.findById(14);
        assert productDTO != null : "productDTO null";
        this.iCartService.handleAddProductToCart("193", productDTO, 1, () -> "test", request, response);

        mockMvc.perform(get("/cart/delete-product/{productId}", 14)
                        .cookie(this.cookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"))
                .andDo(print());
    }

    /**
     * Test trường hợp xoá giỏ hàng thất bại
     * -> không tìm user trong db : invalidUser
     */
    @Test
    @WithMockUser(username = "invalidUser", authorities = "USER")
    @DisplayName("/cart/clear")
    void clearCartOfUser_WithInvalidUser() throws Exception {
        mockMvc.perform(get("/cart/clear")
                        .cookie(this.cookie))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Test trường hợp xoá giỏ hàng thất bại
     * -> không tìm thấy giỏ hàng trong db : cart_id = 123
     */
    @Test
    @DisplayName("/cart/clear")
    void clearCart_WithInvalidCartId() throws Exception {
        Cookie cookie123 = new Cookie("cart_id", "123");
        cookie123.setPath("/");
        cookie123.setMaxAge(60 * 60);
        mockMvc.perform(get("/cart/clear")
                        .cookie(cookie123))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

}
