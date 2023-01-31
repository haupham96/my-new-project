package com.example.vnpayservice.app.exception_handler;

import com.example.vnpayservice.app.exception.CartNotFoundException;
import com.example.vnpayservice.app.exception.InvalidRequestBodyException;
import com.example.vnpayservice.app.exception.NoProductException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidRequestBodyException.class)
    public Map<String, String> handleNoRequestBodyException(InvalidRequestBodyException ex) {
        Map<String, String> err = new HashMap<>();
        err.put("error", ex.getMessage());
        return err;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoProductException.class)
    public Map<String, String> handleNoProductException(NoProductException ex) {
        Map<String, String> err = new HashMap<>();
        err.put("error", ex.getMessage());
        return err;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CartNotFoundException.class)
    public Map<String, String> handleCartNotFoundException(CartNotFoundException ex) {
        Map<String, String> err = new HashMap<>();
        err.put("error", ex.getMessage());
        return err;
    }
}
