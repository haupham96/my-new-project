package com.example.productservice.app.exception;

public class ProductExistedException extends Exception{
    public ProductExistedException(String message) {
        super(message);
    }
}
