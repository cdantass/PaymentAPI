# 🥑 AbacatePay Payment API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-Tracing-7B61FF?style=for-the-badge&logo=opentelemetry&logoColor=white)

API REST para processamento de pagamentos Pix por meio da AbacatePay.

A aplicação cria cobranças, retorna o QR Code e o código Pix copia e cola, registra as transações no PostgreSQL e acompanha automaticamente a confirmação do pagamento.

## 🔄 Fluxo completo do pagamento

```mermaid
flowchart LR
    C[Cliente] -->|Solicita pagamento| API[Payment API]
    API -->|Valida os dados| V{Dados válidos?}

    V -->|Não| C
    V -->|Sim| ABA[AbacatePay]

    ABA -->|QR Code e Pix copia e cola| API
    API -->|Salva como PENDING| DB[(PostgreSQL)]
    API -->|Retorna a cobrança| C

    C -->|Realiza o Pix| ABA
    ABA -->|Envia webhook| API

    API -->|Valida o evento| W{Webhook válido?}
    W -->|Não| ABA
    W -->|Sim| DB

    DB -->|Atualiza PENDING para PAID| API
    API -->|Pagamento confirmado| C
    API -->|Libera pedido ou serviço| S[Aplicação integrada]
```

A criação e a confirmação são etapas independentes:

```text
CREATED → PENDING → PAID
                    ↘ EXPIRED
                    ↘ REFUNDED
```

O pagamento começa como `PENDING`. Após o cliente realizar o Pix, a AbacatePay envia um webhook para a Payment API, que valida o evento e atualiza automaticamente o status para `PAID`.

## ✨ Recursos

- Criação de cobranças Pix
- QR Code e Pix copia e cola
- Consulta de pagamentos por ID
- Persistência no PostgreSQL
- Confirmação automática por webhook
- Validação e tratamento de erros
- Health check com Spring Boot Actuator
- Rastreamento com `trace_id` e `span_id`
- Documentação com Swagger

## 🔗 Endpoints

```http
POST /payments/pix
GET  /payments/{id}
POST /webhooks/abacatepay
GET  /actuator/health
```

## 🛠️ Tecnologias

`Java 21` · `Spring Boot 4` · `PostgreSQL` · `JPA` · `WebClient`  
`OpenTelemetry` · `Micrometer Tracing` · `Actuator` · `Swagger` · `Maven`

## 📌 Progresso

- [x] Cria Persistência e consulta
- [x] Observabilidade e health check
- [ ] Confirmação automática por webhook
- [ ] Devolução de pagamentos
- [ ] Testes automatizados
- [ ] Docker e deploy

## 👨‍💻 Autor

Desenvolvido por **Cauã Dantas Barbosa** 