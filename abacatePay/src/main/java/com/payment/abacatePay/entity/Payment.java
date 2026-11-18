package com.payment.abacatePay.entity;

import com.payment.abacatePay.entity.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_external_id",
                        columnNames = "external_id"
                ),
                @UniqueConstraint(
                        name = "uk_payment_abacatepay_id",
                        columnNames = "abacatepay_id"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, updatable = false)
    private String externalId;

    @Column(name = "abacatepay_id", nullable = false, updatable = false)
    private String abacatePayId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false, length = 140)
    private String description;

    @Lob
    @Column(name = "br_code", nullable = false, columnDefinition = "TEXT")
    private String brCode;

    @Lob
    @Column(
            name = "br_code_base64",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String brCodeBase64;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "development_mode", nullable = false)
    private Boolean developmentMode;

    @Column(name = "platform_fee")
    private Integer platformFee;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getAbacatePayId() {
        return abacatePayId;
    }

    public void setAbacatePayId(String abacatePayId) {
        this.abacatePayId = abacatePayId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrCode() {
        return brCode;
    }

    public void setBrCode(String brCode) {
        this.brCode = brCode;
    }

    public String getBrCodeBase64() {
        return brCodeBase64;
    }

    public void setBrCodeBase64(String brCodeBase64) {
        this.brCodeBase64 = brCodeBase64;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Boolean getDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(Boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public Integer getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(Integer platformFee) {
        this.platformFee = platformFee;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}