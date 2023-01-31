package com.example.vnpayservice.app.service;

import com.example.vnpayservice.app.dto.PayRequest;
import com.example.vnpayservice.app.exception.CartNotFoundException;
import com.example.vnpayservice.app.exception.InvalidRequestBodyException;
import com.example.vnpayservice.app.exception.NoProductException;
import com.example.vnpayservice.app.exception.TransactionFailedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface IVnPayService {
    Map<String, String> getPayUrl(HttpServletRequest req, PayRequest payRequest) throws InvalidRequestBodyException, CartNotFoundException, NoProductException, JsonProcessingException;

    Map<String, String> handleAfterPay(HttpServletRequest request) throws TransactionFailedException, JsonProcessingException;

}
