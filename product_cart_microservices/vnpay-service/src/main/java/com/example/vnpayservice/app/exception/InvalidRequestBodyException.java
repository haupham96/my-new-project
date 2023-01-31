package com.example.vnpayservice.app.exception;

public class InvalidRequestBodyException extends Exception{
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
