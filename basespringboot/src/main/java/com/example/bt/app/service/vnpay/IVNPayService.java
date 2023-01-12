package com.example.bt.app.service.vnpay;

import com.example.bt.app.exception.CartNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author : HauPV
 * service cho vnpay
 */
public interface IVNPayService {
    String handlePay(String orderDetail, String cartId, HttpServletRequest req) throws CartNotFoundException;

    Map<String, String> handleAfterPay(String cartId, HttpServletRequest request) throws CartNotFoundException;

    String getTotalMoney(String cartId) throws CartNotFoundException;
}
