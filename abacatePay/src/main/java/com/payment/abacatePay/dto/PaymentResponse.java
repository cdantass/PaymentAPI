package com.payment.abacatePay.dto;


import com.payment.abacatePay.entity.enums.PaymentStatus;

import java.time.OffsetDateTime;

public record PaymentResponse(

        Long id,
        String externalId,
        String abacatePayId,
        Integer amount,
        String description,
        String brCode,
        String brCodeBase64,
        PaymentStatus status,
        Boolean developmentMode,
        Integer platformFee,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime expiresAt

) {
}