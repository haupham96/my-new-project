
package com.example.bt.app.controller.screen;

import com.example.bt.app.exception.CartNotFoundException;
import com.example.bt.app.service.cart.ICartService;
import com.example.bt.app.service.vnpay.IVNPayService;
import com.example.bt.common.VNPayParam;
import com.example.bt.utils.CookieUtils;
import com.example.bt.utils.VNPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author : HauPV
 * Controller cho chức năng thanh toán VNPay
 */
@Slf4j
@Controller
@CrossOrigin(origins = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html/**")
@RequestMapping("/pay")
public class VNPayController {

    @Autowired
    private ICartService iCartService;

    @Autowired
    private IVNPayService iVNPayService;

    //   Controller điều hướng đến form nhập thông tin thanh toán
    @GetMapping("/form")
    public String payForm(Model model,
                          @CookieValue(value = CookieUtils.CART_ID_KEY, defaultValue = "0") String cartId) throws CartNotFoundException {
        String totalMoney = this.iVNPayService.getTotalMoney(cartId);
        model.addAttribute("totalMoney", totalMoney);
        return "/pay/pay-form";
    }

    //    Controller thực hiện thanh toán VNPay
    @PostMapping("")
    public String payRequest(HttpServletRequest req,
                             @CookieValue(value = CookieUtils.CART_ID_KEY, defaultValue = "0") String cartId,
                             @RequestParam(
                                     required = false,
                                     defaultValue = "Khách hàng Thanh toán tiền điện thoại") String orderDetail)
            throws CartNotFoundException {
        log.info("class - VNPayController");
        log.info("method : payRequest()");
        String paymentUrl = this.iVNPayService.handlePay(orderDetail, cartId, req);
        log.info("Kết thúc method : payRequest()");
        return "redirect:" + paymentUrl;
    }

    // Controller VNPay sẽ gọi đến khi thanh toán xong hoặc cancel thanh toán .
    @GetMapping("/done")
    public String payDone(HttpServletRequest request,
                          Model model,
                          @CookieValue(value = CookieUtils.CART_ID_KEY, defaultValue = "0") String cartId,
                          HttpServletResponse response) throws CartNotFoundException {
        log.info("class - VNPayController");
        log.info("method : payDone()");
        log.info("IP Address : {}", VNPayUtils.getIpAddress(request));
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        log.info("origin : {}",origin);
        // Xử lý data được VNPay trả lại
        Map<String, String> data = this.iVNPayService.handleAfterPay(cartId, request);

        if (VNPayParam.SUCCESS_CODE.equals(request.getParameter(VNPayParam.RESPONSE_CODE))) {
//            Nếu VnPay trả về code 00 -> Thanh toán thành công .
            try {
                model.addAttribute("successData", data);
                log.info("Khối if (principal != null && principal.getName() != null) ");
//                Nếu là user xác thực trong hệ thống -> xoá giỏ hàng sau khi thanh toán trong DB
                iCartService.handlePaymentSuccess(cartId, response);
            } catch (Exception ex) {
                log.info("error {}", ex.getMessage());
            }
            log.info("Kết thúc khối if (principal != null && principal.getName() != null) ");
        } else {
            log.info("Khối else : ResponseCode == 00 ");
//            Trường hợp VnPay không trả về code 00 -> thanh toán thất bại .
            model.addAttribute("error", "Thanh toán thất bại vui lòng thử lại.");
            log.info("Kết thúc khối else : ResponseCode == 00 ");
        }
        log.info("Kết thúc method : payDone()");
        return "/pay/done";
    }
}
