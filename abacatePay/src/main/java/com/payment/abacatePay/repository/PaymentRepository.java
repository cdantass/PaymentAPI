package com.payment.abacatePay.repository;

import com.payment.abacatePay.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByExternalId(String externalId);

    Optional<Payment> findByAbacatePayId(String abacatePayId);

}