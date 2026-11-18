package com.payment.abacatePay.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Pagamento não encontrado com o ID: " + id);
    }
}