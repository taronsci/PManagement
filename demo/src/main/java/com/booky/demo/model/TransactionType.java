package com.booky.demo.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    SELL("Sell"),
    RENT("Rent"),
    EXCHANGE("Exchange"),
    GIVEAWAY("Giveaway");

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
