package com.payment.abacatePay.client;

import com.payment.abacatePay.dto.AbacatePayPixRequest;
import com.payment.abacatePay.dto.AbacatePayPixResponse;
import com.payment.abacatePay.exception.PaymentIntegrationException;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Component
public class AbacatePayClient {

    private static final Logger log =
            LoggerFactory.getLogger(AbacatePayClient.class);

    private final WebClient abacatePayWebClient;
    private final Tracer tracer;

    public AbacatePayClient(
            WebClient abacatePayWebClient,
            Tracer tracer
    ) {
        this.abacatePayWebClient = abacatePayWebClient;
        this.tracer = tracer;
    }

    public AbacatePayPixResponse createPix(
            AbacatePayPixRequest request
    ) {
        Span span = tracer
                .nextSpan()
                .name("abacatepay.create-pix")
                .tag(
                        "payment.external_id",
                        request.data().externalId()
                )
                .tag(
                        "payment.amount",
                        String.valueOf(request.data().amount())
                )
                .start();

        try (Tracer.SpanInScope ignored =
                     tracer.withSpan(span)) {

            log.info(
                    "Enviando solicitação de criação de Pix para a AbacatePay. "
                            + "externalId={}, amount={}",
                    request.data().externalId(),
                    request.data().amount()
            );

            AbacatePayPixResponse response = abacatePayWebClient
                    .post()
                    .uri("/v2/transparents/create")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            clientResponse -> clientResponse
                                    .bodyToMono(String.class)
                                    .defaultIfEmpty(
                                            "A AbacatePay não informou os detalhes do erro"
                                    )
                                    .map(responseBody ->
                                            new PaymentIntegrationException(
                                                    "Erro retornado pela AbacatePay. "
                                                            + "HTTP "
                                                            + clientResponse
                                                            .statusCode()
                                                            .value()
                                                            + ": "
                                                            + responseBody
                                            )
                                    )
                    )
                    .bodyToMono(AbacatePayPixResponse.class)
                    .block();

            validateResponse(response);

            span.tag(
                    "abacatepay.payment_id",
                    response.data().id()
            );

            span.tag(
                    "payment.status",
                    response.data().status()
            );

            log.info(
                    "Cobrança Pix recebida da AbacatePay. "
                            + "externalId={}, abacatePayId={}, status={}",
                    request.data().externalId(),
                    response.data().id(),
                    response.data().status()
            );

            return response;

        } catch (PaymentIntegrationException exception) {
            span.error(exception);

            log.error(
                    "A AbacatePay rejeitou a criação da cobrança Pix. "
                            + "externalId={}, mensagem={}",
                    request.data().externalId(),
                    exception.getMessage()
            );

            throw exception;

        } catch (WebClientRequestException exception) {
            span.error(exception);

            log.error(
                    "Falha de conexão com a AbacatePay. externalId={}",
                    request.data().externalId(),
                    exception
            );

            throw new PaymentIntegrationException(
                    "Não foi possível conectar à AbacatePay",
                    exception
            );

        } catch (Exception exception) {
            span.error(exception);

            log.error(
                    "Erro inesperado durante a criação da cobrança Pix. "
                            + "externalId={}",
                    request.data().externalId(),
                    exception
            );

            throw new PaymentIntegrationException(
                    "Não foi possível criar a cobrança Pix",
                    exception
            );

        } finally {
            span.end();
        }
    }

    private void validateResponse(
            AbacatePayPixResponse response
    ) {
        if (response == null) {
            throw new PaymentIntegrationException(
                    "A AbacatePay retornou uma resposta vazia"
            );
        }

        if (response.error() != null
                && !response.error().isNull()) {
            throw new PaymentIntegrationException(
                    "Erro retornado pela AbacatePay: "
                            + response.error()
            );
        }

        if (response.data() == null) {
            throw new PaymentIntegrationException(
                    "A resposta da AbacatePay não contém os dados da cobrança"
            );
        }

        if (response.data().id() == null
                || response.data().id().isBlank()) {
            throw new PaymentIntegrationException(
                    "A AbacatePay não retornou o ID da cobrança"
            );
        }

        if (response.data().brCode() == null
                || response.data().brCode().isBlank()) {
            throw new PaymentIntegrationException(
                    "A AbacatePay não retornou o código Pix"
            );
        }

        if (response.data().brCodeBase64() == null
                || response.data().brCodeBase64().isBlank()) {
            throw new PaymentIntegrationException(
                    "A AbacatePay não retornou o QR Code"
            );
        }
    }
}