package com.example.bt.app.service.vnpay;

import com.example.bt.app.entity.Cart;
import com.example.bt.app.exception.CartNotFoundException;
import com.example.bt.app.service.cart.ICartService;
import com.example.bt.common.VNPayParam;
import com.example.bt.utils.VNPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : HauPV
 * service cho vnpay
 */
@Slf4j
@PropertySource("classpath:application.properties")
@Service
public class VNPayServiceImpl implements IVNPayService {

    @Autowired
    private VNPayUtils vnPayUtils;

    @Autowired
    private ICartService iCartService;

    @Value("${app.environment}")
    private String environment;

    //    Chuyển các param cần thiết để gửi request sang cho vnpay dưới dạng 1 url hoàn chỉnh
    @Override
    public String handlePay(String orderDetail, String cartId, HttpServletRequest req) throws CartNotFoundException {
//        Danh sách các param cần thiết mà vnpay yêu cầu
        Cart cart = this.iCartService.findByCartId(Integer.valueOf(cartId));
        if (cart == null) {
            throw new CartNotFoundException("Không tìm thấy giỏ hàng - " + cartId);
        }
        this.vnPayUtils.setEnvironment(this.environment);
        log.info("class - VNPayServiceImpl");
        log.info("method - handlePay()");

        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String vnpTxnRef = String.valueOf(new Date().getTime());
        String vnpIpAddr = VNPayUtils.getIpAddress(req);
        String vnpTmnCode = this.vnPayUtils.getVnpTmnCode();

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put(VNPayParam.VERSION, vnpVersion);
        vnpParams.put(VNPayParam.COMMAND, vnpCommand);
        vnpParams.put(VNPayParam.CODE, vnpTmnCode);

        long amount = cart.getTotalPrice().longValue() * 100;
        vnpParams.put(VNPayParam.AMOUNT, String.valueOf(amount));

        vnpParams.put(VNPayParam.CURRENCY, VNPayParam.CURRENCY_VND);
        vnpParams.put(VNPayParam.BILL_NUMBER, vnpTxnRef);
        vnpParams.put(VNPayParam.ORDER_INFO, orderDetail);
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

        log.info("kết thúc method - handlePay()");
        return queryUrl.toString();
    }

    //    Xử lý data vnpay trả về sau khi thanh toán
    @Override
    public Map<String, String> handleAfterPay(String cartId,
                                              HttpServletRequest request) throws CartNotFoundException {
        log.info("class - VNPayServiceImpl");
        log.info("method - handlePay()");
//      Chuyển đổi thông tin trả về bằng Map
        Cart cart = this.iCartService.findByCartId(Integer.valueOf(cartId));
        if (cart == null) {
            throw new CartNotFoundException("Không tìm thấy giỏ hàng : " + cartId);
        }

        Map<String, String> data = new HashMap<>();
        data.put("Ngân hàng giao dịch : ", request.getParameter(VNPayParam.BANK_CODE));
        data.put("Thông tin giao dịch : ", request.getParameter(VNPayParam.ORDER_INFO));
        NumberFormat numberFormat = NumberFormat.getInstance();
//        Format kiểu số ngăn cách bằng dấu phẩy của hàng nghìn cho dễ đọc
        String totalMoney = numberFormat.format(cart.getTotalPrice().longValue()) + " " + VNPayParam.CURRENCY_VND;
        data.put("Số tiền thanh toán : ", totalMoney);
        data.put("Hình thức thanh toán : ", request.getParameter(VNPayParam.CARD_TYPE));
        data.put("Số hoá đơn : ", request.getParameter(VNPayParam.BILL_NUMBER));
        data.put("Mã giao dịch : ", request.getParameter(VNPayParam.TRANSACTION_NUMBER));

        log.info("kết thúc method - handlePay()");
        return data;
    }

    /* Lấy tổng tiền của giỏ hàng để hiện thông tin thanh toán */
    @Override
    public String getTotalMoney(String cartId) throws CartNotFoundException {
        Cart cart = this.iCartService.findByCartId(Integer.valueOf(cartId));
        if (cart != null) {
            /* Nếu có giỏ hàng -> lấy ra tổng tiền */
            log.info("khối if : cart != null");
            NumberFormat numberFormat = NumberFormat.getInstance();
            log.info("kết thúc khối if : cart != null");
            return numberFormat.format(cart.getTotalPrice());
        } else {
            /* Không tìm thấy giỏ hàng */
            log.info("khối else : cart != null => Throw CartNotFoundException");
            throw new CartNotFoundException("Không tìm thấy giỏ hàng - " + cartId);
        }
    }
}
