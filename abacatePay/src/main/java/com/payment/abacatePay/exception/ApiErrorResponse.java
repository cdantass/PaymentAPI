package com.payment.abacatePay.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiErrorResponse(

        int status,
        String error,
        String message,
        OffsetDateTime timestamp,
        Map<String, String> validationErrors

) {
}