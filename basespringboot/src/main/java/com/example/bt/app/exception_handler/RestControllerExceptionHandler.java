package com.example.bt.app.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<String, String> handleValidateException(BindException ex) {
        Map<String, String> error = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage()));
        return error;
    }
}
