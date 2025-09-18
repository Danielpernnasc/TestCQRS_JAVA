package com.Satander.CQRS.bank;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        this.amount = amount.setScale(2);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}