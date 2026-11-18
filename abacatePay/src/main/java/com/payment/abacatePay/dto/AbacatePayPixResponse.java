package com.payment.abacatePay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacatePayPixResponse(

        Data data,
        JsonNode error

) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(

            String id,
            Integer amount,
            String status,
            Boolean devMode,
            String brCode,
            String brCodeBase64,
            Integer platformFee,
            String receiptUrl,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime expiresAt

    ) {
    }
}