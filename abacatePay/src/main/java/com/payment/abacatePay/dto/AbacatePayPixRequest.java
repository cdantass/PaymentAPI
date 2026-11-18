package com.payment.abacatePay.dto;

public record AbacatePayPixRequest(
        String method,
        Data data
) {

    public record Data(
            Integer amount,
            String description,
            Integer expiresIn,
            String externalId
    ) {
    }
}