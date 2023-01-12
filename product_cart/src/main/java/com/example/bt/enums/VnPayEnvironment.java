package com.example.bt.enums;

public enum VnPayEnvironment {
    LOCAL("local"),
    DEV("dev"),
    PRODUCT("product");

    private final String value;

    VnPayEnvironment(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
