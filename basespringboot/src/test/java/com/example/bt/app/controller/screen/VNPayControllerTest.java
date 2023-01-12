package com.example.bt.app.controller.screen;

import com.example.bt.app.entity.Cart;
import com.example.bt.app.repository.ICartRepository;
import com.example.bt.app.service.product.IProductService;
import com.example.bt.common.VNPayParam;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author HauPV
 * JUnit Test cho VNPayController
 * => Done
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class VNPayControllerTest {

    @Autowired
    IProductService iProductService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ICartRepository iCartRepository;

    Cookie cookie;
    Cart cart;

    @BeforeEach
    void setUp() {
        this.cookie = new Cookie("cart_id", "193");
        cookie.setMaxAge(60 * 2); // 2 phút
        cookie.setPath("/");

        cart = new Cart();
        cart.setTotalPrice(BigDecimal.valueOf(1000000));
        this.iCartRepository.saveAndFlush(cart);
    }

    @AfterEach
    void clearSetup() {
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp điều hướng thất bại đến trang nhập thông tin thanh toán
     * -> chưa có giỏ hàng trong cookie
     */
    @Test
    @DisplayName("GET /pay/form")
    void payForm_FailWithNoCart() throws Exception {

        mockMvc.perform(get("/pay/form"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Trường hợp điều hướng thành công đến trang nhập thông tin thanh toán
     */
    @Test
    @DisplayName("GET /pay/form")
    void payForm_Success() throws Exception {
        mockMvc.perform(get("/pay/form").cookie(this.cookie))
                .andExpect(model().attributeExists("totalMoney"))
                .andExpect(view().name("/pay/pay-form"))
                .andDo(print());
    }

    /**
     * Trường hợp thanh toán thất bại
     * -> không có giỏ hàng
     */
    @Test
    @DisplayName("POST /pay")
    void payRequestRedirectWithNoProduct() throws Exception {
        mockMvc.perform(post("/pay"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("error-page"))
                .andDo(print());
    }

    /**
     * Trường hợp nhập thông tin thanh toán thành công
     */
    @Test
    @DisplayName("POST /pay")
    void payRequestSuccess() throws Exception {
        mockMvc.perform(post("/pay").cookie(this.cookie))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }

    /**
     * Trường hợp thanh toán thất bại với RESPONSE_CODE = "24" : Khách hàng huỷ giao dịch .
     */
    @Test
    @DisplayName("GET /done")
    void payFail() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(VNPayParam.BANK_CODE, "NCB");
        params.add(VNPayParam.ORDER_INFO, "Thanh toán tiền mua điện thoại");
        params.add(VNPayParam.CARD_TYPE, "ATM");
        params.add(VNPayParam.BILL_NUMBER, "25086603");
        params.add(VNPayParam.TRANSACTION_NUMBER, "VNP123123");
        params.add(VNPayParam.RESPONSE_CODE, "24");

        mockMvc.perform(get("/pay/done")
                        .cookie(new Cookie("cart_id", String.valueOf(this.cart.getCartId())))
                        .params(params))
                .andExpect(view().name("/pay/done"))
                .andExpect(model().attributeExists("error"))
                .andDo(print());
    }

    /**
     * Trường hợp thanh toán thành công và trả lại chi tiết giao dịch
     */
    @Test
    @DisplayName("GET /done")
    void payDone() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(VNPayParam.BANK_CODE, "NCB");
        params.add(VNPayParam.ORDER_INFO, "Thanh toán tiền mua điện thoại");
        params.add(VNPayParam.CARD_TYPE, "ATM");
        params.add(VNPayParam.BILL_NUMBER, "25086603");
        params.add(VNPayParam.TRANSACTION_NUMBER, "VNP123123");
        params.add(VNPayParam.RESPONSE_CODE, VNPayParam.SUCCESS_CODE);

        mockMvc.perform(get("/pay/done")
                        .cookie(new Cookie("cart_id", String.valueOf(this.cart.getCartId())))
                        .params(params))
                .andExpect(view().name("/pay/done"))
                .andExpect(model().attributeExists("successData"))
                .andDo(print());
    }

}
