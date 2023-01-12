package com.example.bt.utils;

import com.example.bt.common.EnvironmentName;
import com.example.bt.enums.VnPayEnvironment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author : HauPV
 * lớp util VNPay cung cấp để mã hoá data gửi sang cho VNPay
 */

@Component
@Getter
@Slf4j
@PropertySource("classpath:vnpay_resources/vnpay-local.properties")
@NoArgsConstructor
public class VNPayUtils {

    @Autowired
    Environment env;
    private static final Random rnd = new Random();
    private static final String LOG_CATCH_BLOCK_MESSAGE = "khối catch NoSuchAlgorithmException";

    //    Trang cần gửi request đến để thanh toán
    private String vnpPayUrl;
    //    Trang mà VnPay sẽ trả về data sau khi thanh toán xong
    private String vnpReturnUrl;
    //    ID của ứng dụng đăng kí với VNPay
    private String vnpTmnCode;
    //    Mã xác thực đăng kí với VNPay
    private String vnpHashSecret;

    public void setEnvironment(String environment) {
        switch (environment) {
            case EnvironmentName.LOCAL:
                this.vnpPayUrl = env.getProperty("vnpay.local.url");
                this.vnpReturnUrl = env.getProperty("vnpay.local.return-url");
                this.vnpTmnCode = env.getProperty("vnpay.local.tmn-code");
                this.vnpHashSecret = env.getProperty("vnpay.local.hash-secret");
                break;
            case EnvironmentName.DEV:
                this.vnpPayUrl = env.getProperty("vnpay.dev.url");
                this.vnpReturnUrl = env.getProperty("vnpay.dev.return-url");
                this.vnpTmnCode = env.getProperty("vnpay.dev.tmn-code");
                this.vnpHashSecret = env.getProperty("vnpay.dev.hash-secret");
                break;
            case EnvironmentName.PRODUCT:
                this.vnpPayUrl = env.getProperty("vnpay.product.url");
                this.vnpReturnUrl = env.getProperty("vnpay.product.return-url");
                this.vnpTmnCode = env.getProperty("vnpay.product.tmn-code");
                this.vnpHashSecret = env.getProperty("vnpay.product.hash-secret");
                break;
            default:
                break;
        }
    }

    //    Mã hoá thông tin dưới dạng thuật toán md5
    public static String md5(String message) {
        log.info(VNPayUtils.class.getSimpleName());
        log.info("method - md5()");
        String digest = null;
        try {
            log.info("khối try ");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
            log.info("kết thúc khối try ");
        } catch (NoSuchAlgorithmException ex) {
            log.info(LOG_CATCH_BLOCK_MESSAGE);
            log.error("error : {} ", ex.getMessage());
            digest = "";
            log.info("kết thúc khối catch -> Exception : NoSuchAlgorithmException");
        }
        log.info("kết thúc method - md5()");
        return digest;
    }

    //    Mã hoá thông tin theo thuật toán Sha256
    public static String sha256(String message) {
        log.info(VNPayUtils.class.getSimpleName());
        log.info("method - Sha256()");
        String digest = null;
        try {
            log.info("khối try");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));

            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();
            log.info("kết thúc khối try");
        } catch (NoSuchAlgorithmException ex) {
            log.info(LOG_CATCH_BLOCK_MESSAGE);
            digest = "";
            log.info("kết thúc khối catch -> NoSuchAlgorithmException");
        }
        log.info("kết thúc method - Sha256()");
        return digest;
    }

    //    Mã hoá thông tin theo thuật toán hmacSHA512
    public static String hmacSHA512(final String key, final String data) {
        log.info("class - VNPayUtils");
        log.info("method - hmacSHA512()");
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            log.info("Kết thúc khối try");
            log.info("kết thúc method - hmacSHA512()");
            return sb.toString();
        } catch (Exception ex) {
            log.info(LOG_CATCH_BLOCK_MESSAGE);
            log.info("kết thúc khối catch -> Exception");
            log.info("kết thúc method - hmacSHA512()");
            return "";
        }
    }

    //    Mã hoá thông tin theo thuật toán hmacSHA512
    public String hashAllFields(Map<String, String> fields) {
        log.info(VNPayUtils.class.getSimpleName());
        log.info("method - hashAllFields()");
        // Tạo ra list tên các key và sort theo thứ tự alphabet
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                Nếu fieldValue có giá trị -> nối vào StringBuilder
                log.info("khối if : (fieldValue != null) && (fieldValue.length() > 0)");
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
                log.info("kết thúc khối if : (fieldValue != null) && (fieldValue.length() > 0)");
            }
            if (itr.hasNext()) {
//                Nếu list fieldNames còn dữ liệu -> nối vào StringBuilder
                log.info("khối if : itr.hasNext()");
                sb.append("&");
                log.info("kết thúc khối if : itr.hasNext()");
            }
        }
        log.info("kết thúc method - hashAllFields()");
        return hmacSHA512(this.vnpHashSecret, sb.toString());
    }

    //    Lấy địa chỉ ip của ứng dụng
    public static String getIpAddress(HttpServletRequest request) {
        log.info(VNPayUtils.class.getSimpleName());
        log.info("method - getIpAddress()");
        String ipAdress;
        try {
            log.info("khối try");
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
//                Nếu request ko có header với key là X-FORWARDED-FOR
                log.info("khối if ipAdress == null");
                ipAdress = request.getRemoteAddr();
                log.info("kết thúc khối if ipAdress == null");
            }
            log.info("kết thúc khối try");
        } catch (Exception e) {
            log.info("khối catch Exception");
            ipAdress = "Invalid IP:" + e.getMessage();
            log.info("kết thúc khối catch -> Exception");
        }
        log.info("kết thúc method - getIpAddress()");
        return ipAdress;
    }

    //    Tạo ra 1 dãy số ngẫu nhiên
    public static String getRandomNumber(int len) {
        log.info(VNPayUtils.class.getSimpleName());
        log.info("method - getRandomNumber()");
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        log.info("kết thúc method - getRandomNumber()");
        return sb.toString();
    }
}
