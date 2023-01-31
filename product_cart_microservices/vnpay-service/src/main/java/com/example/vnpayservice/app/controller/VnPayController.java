package com.example.vnpayservice.app.controller;

import com.example.vnpayservice.app.dto.PayRequest;
import com.example.vnpayservice.app.exception.CartNotFoundException;
import com.example.vnpayservice.app.exception.InvalidRequestBodyException;
import com.example.vnpayservice.app.exception.NoProductException;
import com.example.vnpayservice.app.exception.TransactionFailedException;
import com.example.vnpayservice.app.service.IVnPayService;
import com.example.vnpayservice.common.VNPayParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = {"https://sandbox.vnpayment.vn"})
@RequestMapping("/api/pay")
public class VnPayController {
    @Autowired
    private IVnPayService iVnPayService;

    @PostMapping("")
    public Map<String, String> getPayUrl(HttpServletRequest req,
                                         @RequestBody(required = false) PayRequest payRequest) throws InvalidRequestBodyException, NoProductException, CartNotFoundException, JsonProcessingException {
        return this.iVnPayService.getPayUrl(req, payRequest);
    }

    @GetMapping("/done")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> handlePayDone(HttpServletRequest request) throws TransactionFailedException, JsonProcessingException {
        return this.iVnPayService.handleAfterPay(request);
    }
}
