package com.Satander.CQRS.bank;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String type;
    private Instant createdAt;

   



    public Transaction(String accountId, BigDecimal amount, TransactionType type) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.amount = amount.setScale(2);
        this.type = type.name();
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
