package com.example.vnpayservice.app.service;

import com.example.vnpayservice.app.dto.CartDTO;
import com.example.vnpayservice.app.dto.PayRequest;
import com.example.vnpayservice.app.dto.ProductDTO;
import com.example.vnpayservice.app.dto.VnPayOrderInfo;
import com.example.vnpayservice.app.exception.CartNotFoundException;
import com.example.vnpayservice.app.exception.InvalidRequestBodyException;
import com.example.vnpayservice.app.exception.NoProductException;
import com.example.vnpayservice.app.exception.TransactionFailedException;
import com.example.vnpayservice.common.VNPayParam;
import com.example.vnpayservice.common.VNPayUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VnPayServiceImpl implements IVnPayService {

    @Autowired
    private VNPayUtils vnPayUtils;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, String> getPayUrl(HttpServletRequest req, PayRequest payRequest) throws InvalidRequestBodyException, CartNotFoundException, NoProductException, JsonProcessingException {
        if (payRequest == null) {
            throw new InvalidRequestBodyException("Empty request body");
        }
        CartDTO cart;
        try {
            cart = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/cart/{id}", payRequest.getCartId())
                    .retrieve()
                    .bodyToMono(CartDTO.class)
                    .block();
        } catch (Exception ex) {
            log.info("Exception : " + ex.getMessage());
            cart = null;
        }

        if (cart == null) {
            throw new CartNotFoundException("Not found cart with id - " + payRequest.getCartId());
        }
        if (cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new NoProductException("Products is empty with cart - " + cart.getId());
        }
        vnPayUtils.setEnvironment();

        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String vnpTxnRef = String.valueOf(new Date().getTime());
        String vnpIpAddr = VNPayUtils.getIpAddress(req);
        String vnpTmnCode = this.vnPayUtils.getVnpTmnCode();

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put(VNPayParam.VERSION, vnpVersion);
        vnpParams.put(VNPayParam.COMMAND, vnpCommand);
        vnpParams.put(VNPayParam.CODE, vnpTmnCode);
        vnpParams.put(VNPayParam.BILL_NUMBER, vnpTxnRef);

        long totalPrice = cart.getTotalPrice();
        long amount = totalPrice * 100;

        VnPayOrderInfo orderInfo = VnPayOrderInfo.builder()
                .cartId(cart.getId())
                .totalPrice(totalPrice)
                .note(payRequest.getNote())
                .build();
        vnpParams.put(VNPayParam.ORDER_INFO, objectMapper.writeValueAsString(orderInfo));
        /* get total price from cart */
        vnpParams.put(VNPayParam.AMOUNT, String.valueOf(amount));
        vnpParams.put(VNPayParam.CURRENCY, VNPayParam.CURRENCY_VND);
        vnpParams.put(VNPayParam.LOCALE, VNPayParam.LOCALE_VN);
        vnpParams.put(VNPayParam.RETURN_URL, this.vnPayUtils.getVnpReturnUrl());
        vnpParams.put(VNPayParam.IP_ADDRESS, vnpIpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(VNPayParam.TIMEZONE_GMT_7));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put(VNPayParam.CREATE_DATE, vnpCreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put(VNPayParam.EXPIRE_DATE, vnpExpireDate);

        // Build data thành 1 request url
        StringBuilder queryUrl = new StringBuilder();
        queryUrl.append(this.vnPayUtils.getVnpPayUrl()).append("?");

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                Nếu danh sách các param không bị trống -> build thành 1 url
                log.info("khối if : (fieldValue != null) && (fieldValue.length() > 0) ");
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                queryUrl.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                queryUrl.append('=');
                queryUrl.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
//                    Nếu còn data -> tiếp tục build url
                    log.info("Khối if : itr.hasNext()");
                    queryUrl.append('&');
                    hashData.append('&');
                    log.info("Kết thúc khối if : itr.hasNext()");
                }
                log.info("kết thúc khối if : (fieldValue != null) && (fieldValue.length() > 0) ");
            }
        }
//        hash mã xác thực vnpay
        String vnpSecureHash = VNPayUtils.hmacSHA512(this.vnPayUtils.getVnpHashSecret(), hashData.toString());
        queryUrl.append("&").append(VNPayParam.SECURITY_HASH).append("=").append(vnpSecureHash);
        vnpParams.put(VNPayParam.SECURITY_HASH, vnpSecureHash);

        log.info("kết thúc method - handlePay()");
        Map<String, String> result = new HashMap<>();
        result.put("url", queryUrl.toString());
        return result;
    }

    @Override
    public Map<String, String> handleAfterPay(HttpServletRequest request) throws TransactionFailedException, JsonProcessingException {
        log.info("class - VNPayServiceImpl");
        log.info("method - handlePay()");
        String statusCode = request.getParameter(VNPayParam.RESPONSE_CODE);
        if (!VNPayParam.SUCCESS_CODE.equals(statusCode)) {
            throw new TransactionFailedException("Transaction is failed , please try again later .");
        }
        VnPayOrderInfo orderInfo = objectMapper.readValue(request.getParameter(VNPayParam.ORDER_INFO), VnPayOrderInfo.class);

        Map<String, String> data = new HashMap<>();
        data.put("Ngân hàng giao dịch", request.getParameter(VNPayParam.BANK_CODE));
        data.put("Thông tin giao dịch", orderInfo.getNote());
        NumberFormat numberFormat = NumberFormat.getInstance();
//        Format kiểu số ngăn cách bằng dấu phẩy của hàng nghìn cho dễ đọc
        long totalPrice = orderInfo.getTotalPrice();
        String totalMoney = numberFormat.format(totalPrice) + " " + VNPayParam.CURRENCY_VND;
        data.put("Số tiền thanh toán", totalMoney);
        data.put("Hình thức thanh toán", request.getParameter(VNPayParam.CARD_TYPE));
        data.put("Số hoá đơn", request.getParameter(VNPayParam.BILL_NUMBER));
        data.put("Mã giao dịch", request.getParameter(VNPayParam.TRANSACTION_NUMBER));
        log.info("kết thúc method - handlePay()");

        ResponseEntity<Void> response = webClientBuilder.build()
                .delete()
                .uri("http://localhost:8080/api/cart/{cartId}", orderInfo.getCartId())
                .retrieve()
                .toBodilessEntity()
                .block();
        assert response != null;
        log.info("status code : " + response.getStatusCode().value());
        return data;
    }
}
