package com.example.orderservice.controller_advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> exceptionHandler(Exception ex) {
//        Map<String, String> err = new HashMap<>();
//        err.put("error : ", ex.getMessage());
//        return err;
//    }

}
