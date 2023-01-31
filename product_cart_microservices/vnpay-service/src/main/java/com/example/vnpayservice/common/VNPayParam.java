package com.example.vnpayservice.common;

public interface VNPayParam {
    String REQUEST_ID = "vnp_RequestId";
    String VERSION = "vnp_Version";
    String COMMAND = "vnp_Command";
    String CODE = "vnp_TmnCode";
    String AMOUNT = "vnp_Amount";
    String CURRENCY = "vnp_CurrCode";
    String BILL_NUMBER = "vnp_TxnRef";
    String ORDER_INFO = "vnp_OrderInfo";
    String LOCALE = "vnp_Locale";
    String RETURN_URL = "vnp_ReturnUrl";
    String IP_ADDRESS = "vnp_IpAddr";
    String CREATE_DATE = "vnp_CreateDate";
    String EXPIRE_DATE = "vnp_ExpireDate";
    String SECURITY_HASH = "vnp_SecureHash";
    String TRANSACTION_NUMBER = "vnp_TransactionNo";
    String TRANSACTION_TYPE = "vnp_TransactionType";
    String RESPONSE_CODE = "vnp_ResponseCode";
    String BANK_CODE = "vnp_BankCode";
    String CARD_TYPE = "vnp_CardType";
    String SUCCESS_CODE = "00";
    String CURRENCY_VND = "VND";
    String LOCALE_VN = "vn";
    String TIMEZONE_GMT_7 = "Etc/GMT+7";
    String CREATED_BY = "vnp_CreateBy";
}
