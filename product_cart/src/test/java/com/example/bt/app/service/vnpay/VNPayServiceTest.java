package com.example.bt.app.service.vnpay;

import com.example.bt.app.entity.Cart;
import com.example.bt.app.exception.CartNotFoundException;
import com.example.bt.app.repository.ICartRepository;
import com.example.bt.common.VNPayParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.NumberFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Unit Test cho VNPayService
 * => DONE
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VNPayServiceTest {

    @Autowired
    private IVNPayService iVNPayService;

    @Autowired
    private ICartRepository iCartRepository;

    @Mock
    private HttpServletRequest request;
    private final String PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    /**
     * Trường hợp không tìm thấy giỏ hàng trong db
     * -> Exception
     */
    @Test
    @DisplayName("handlePay_FailWithCartNotFound")
    void handlePay_FailWithCartNotFound() {
        assertThrows(CartNotFoundException.class, () -> {
            this.iVNPayService.handlePay("order", "0", request);
        });
    }

    /**
     * Trường hợp tìm thấy giỏ hàng trong db
     */
    @Test
    @DisplayName("handlePay_FailWithCartNotFound")
    void handlePay_Success() {
        Cart cart = new Cart();
        cart.setTotalPrice(new BigDecimal(5000000));
        this.iCartRepository.saveAndFlush(cart);
        assertDoesNotThrow(() -> {
            String queryUrl = this.iVNPayService.handlePay("order", String.valueOf(cart.getCartId()), request);
            assertTrue(queryUrl.length() > 0);
            assertTrue(queryUrl.startsWith(PAY_URL));
            assertTrue(queryUrl.contains(cart.getTotalPrice().toString()));
        });
        /* clear data sau khi test */
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp ko tìm thấy giỏ hàng trong db
     * -> Exception
     */
    @Test
    @DisplayName("handleAfterPay_FailWithCancelled")
    void handleAfterPay_FailWithCancelled() {
        assertThrows(CartNotFoundException.class, () -> {
            this.iVNPayService.handleAfterPay("0", request);
        });
    }

    /**
     * Trường hợp tìm thấy giỏ hàng trong db
     */
    @Test
    @DisplayName("handleAfterPay_FailWithCancelled")
    void handleAfterPay_Success() {
        Cart cart = new Cart();
        cart.setTotalPrice(new BigDecimal(5000000));
        this.iCartRepository.saveAndFlush(cart);
        assertDoesNotThrow(() -> {
            var result = this.iVNPayService.handleAfterPay(String.valueOf(cart.getCartId()), request);
            /* có data trả về */
            assertNotNull(result);
            assertEquals(6, result.size());
            NumberFormat fm = NumberFormat.getInstance();
            String totalPrice = fm.format(cart.getTotalPrice()) + " " + VNPayParam.CURRENCY_VND;
            assertEquals(result.get("Số tiền thanh toán : "), totalPrice);
        });
        /* clear data sau khi test */
        iCartRepository.delete(cart);
    }

    /**
     * Trường hợp ko tìm thấy giỏ hàng trong db
     * -> Exception
     */
    @Test
    @DisplayName("getTotalMoney_WithCartNotFound")
    void getTotalMoney_WithCartNotFound() {
        assertThrows(CartNotFoundException.class, () -> {
            this.iVNPayService.getTotalMoney("0");
        });
    }

    /**
     * Trường hợp tìm thấy giỏ hàng trong db
     */
    @Test
    @DisplayName("getTotalMoney_Success")
    void getTotalMoney_Success() {
        Cart cart = new Cart();
        cart.setTotalPrice(new BigDecimal(5000000));
        this.iCartRepository.saveAndFlush(cart);
        assertDoesNotThrow(() -> {
            String totalMoney = this.iVNPayService.getTotalMoney(String.valueOf(cart.getCartId()));
            NumberFormat numberFormat = NumberFormat.getInstance();
            String cartTotalPrice = numberFormat.format(cart.getTotalPrice());
            assertEquals(totalMoney, cartTotalPrice);
        });
        /* clear data sau khi test */
        iCartRepository.delete(cart);
    }

}
