package com.payment.abacatePay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePixRequest(

        @NotNull(message = "O valor é obrigatório")
        @Min(
                value = 100,
                message = "O valor mínimo é 100 centavos, equivalente a R$ 1,00"
        )
        Integer amount,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(
                max = 140,
                message = "A descrição deve possuir no máximo 140 caracteres"
        )
        String description,

        @Min(
                value = 300,
                message = "A expiração mínima é de 300 segundos"
        )
        Integer expiresIn,

        @Size(
                max = 100,
                message = "O externalId deve possuir no máximo 100 caracteres"
        )
        String externalId

) {
}