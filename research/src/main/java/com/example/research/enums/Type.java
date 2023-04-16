package com.example.mybatis.enums;

import lombok.Getter;

@Getter
public enum Type {
    SYSTEM("sys"),
    LOCAL("localhost");

    private String type;

    Type(String type) {
        this.type = type;
    }

}
