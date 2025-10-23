package com.booky.demo.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BookCondition {
    USED("Used"),
    NEW("New");

    private final String code;

    BookCondition(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
