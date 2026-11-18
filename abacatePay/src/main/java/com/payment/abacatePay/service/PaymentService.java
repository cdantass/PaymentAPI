package com.payment.abacatePay.service;

import com.payment.abacatePay.client.AbacatePayClient;
import com.payment.abacatePay.dto.AbacatePayPixRequest;
import com.payment.abacatePay.dto.AbacatePayPixResponse;
import com.payment.abacatePay.dto.CreatePixRequest;
import com.payment.abacatePay.dto.PaymentResponse;
import com.payment.abacatePay.entity.Payment;
import com.payment.abacatePay.entity.enums.PaymentStatus;
import com.payment.abacatePay.exception.PaymentIntegrationException;
import com.payment.abacatePay.exception.PaymentNotFoundException;
import com.payment.abacatePay.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private static final int DEFAULT_EXPIRATION_SECONDS = 3600;

    private final PaymentRepository paymentRepository;
    private final AbacatePayClient abacatePayClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            AbacatePayClient abacatePayClient
    ) {
        this.paymentRepository = paymentRepository;
        this.abacatePayClient = abacatePayClient;
    }

    @Transactional
    public PaymentResponse createPix(
            CreatePixRequest request
    ) {
        String externalId = resolveExternalId(
                request.externalId()
        );

        int expiresIn = request.expiresIn() == null
                ? DEFAULT_EXPIRATION_SECONDS
                : request.expiresIn();

        log.info(
                "Iniciando criação da cobrança Pix. "
                        + "externalId={}, amount={}, expiresIn={}",
                externalId,
                request.amount(),
                expiresIn
        );

        AbacatePayPixRequest integrationRequest =
                new AbacatePayPixRequest(
                        "PIX",
                        new AbacatePayPixRequest.Data(
                                request.amount(),
                                request.description(),
                                expiresIn,
                                externalId
                        )
                );

        AbacatePayPixResponse integrationResponse =
                abacatePayClient.createPix(
                        integrationRequest
                );

        AbacatePayPixResponse.Data pixData =
                integrationResponse.data();

        Payment payment = new Payment();

        payment.setExternalId(externalId);
        payment.setAbacatePayId(pixData.id());
        payment.setAmount(pixData.amount());
        payment.setDescription(request.description());
        payment.setBrCode(pixData.brCode());
        payment.setBrCodeBase64(pixData.brCodeBase64());
        payment.setStatus(
                convertStatus(pixData.status())
        );
        payment.setDevelopmentMode(
                Boolean.TRUE.equals(pixData.devMode())
        );
        payment.setPlatformFee(pixData.platformFee());
        payment.setCreatedAt(pixData.createdAt());
        payment.setUpdatedAt(pixData.updatedAt());
        payment.setExpiresAt(pixData.expiresAt());

        Payment savedPayment =
                paymentRepository.save(payment);

        log.info(
                "Cobrança Pix salva com sucesso. "
                        + "paymentId={}, externalId={}, "
                        + "abacatePayId={}, status={}",
                savedPayment.getId(),
                savedPayment.getExternalId(),
                savedPayment.getAbacatePayId(),
                savedPayment.getStatus()
        );

        return toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        log.info(
                "Iniciando consulta do pagamento. paymentId={}",
                id
        );

        Payment payment = paymentRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "Pagamento não encontrado. paymentId={}",
                            id
                    );

                    return new PaymentNotFoundException(id);
                });

        log.info(
                "Pagamento encontrado. "
                        + "paymentId={}, externalId={}, status={}",
                payment.getId(),
                payment.getExternalId(),
                payment.getStatus()
        );

        return toResponse(payment);
    }

    private String resolveExternalId(
            String externalId
    ) {
        if (externalId == null
                || externalId.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return externalId.trim();
    }

    private PaymentStatus convertStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            throw new PaymentIntegrationException(
                    "A AbacatePay não retornou o status da cobrança"
            );
        }

        try {
            return PaymentStatus.valueOf(
                    status.toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException exception) {
            throw new PaymentIntegrationException(
                    "Status desconhecido retornado pela AbacatePay: "
                            + status
            );
        }
    }

    private PaymentResponse toResponse(
            Payment payment
    ) {
        return new PaymentResponse(
                payment.getId(),
                payment.getExternalId(),
                payment.getAbacatePayId(),
                payment.getAmount(),
                payment.getDescription(),
                payment.getBrCode(),
                payment.getBrCodeBase64(),
                payment.getStatus(),
                payment.getDevelopmentMode(),
                payment.getPlatformFee(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getExpiresAt()
        );
    }
}