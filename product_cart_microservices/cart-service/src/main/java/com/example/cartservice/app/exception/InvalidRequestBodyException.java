package com.example.cartservice.app.exception;

public class InvalidRequestBodyException extends Exception {
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}
