package com.payment.abacatePay.controller;

import com.payment.abacatePay.dto.CreatePixRequest;
import com.payment.abacatePay.dto.PaymentResponse;
import com.payment.abacatePay.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pix")
    public ResponseEntity<PaymentResponse> createPix(
            @Valid @RequestBody CreatePixRequest request
    ) {
        PaymentResponse response =
                paymentService.createPix(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(
            @PathVariable Long id
    ) {
        PaymentResponse response =
                paymentService.findById(id);

        return ResponseEntity.ok(response);
    }
}