package br.com.dio.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public record MoneyAudit(
        UUID transactionId,
        BankService targetService,
        String description,
        OffsetDateTime createdAt) {

    public MoneyAudit(UUID transactionId, BankService service, String description, Instant now) {
        this(
                transactionId,
                service,
                description,
                OffsetDateTime.ofInstant(now, ZoneOffset.UTC) // conversão de Instant para OffsetDateTime
        );
    }
}
