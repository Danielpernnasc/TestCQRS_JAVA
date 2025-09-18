package com.Satander.CQRS.bank;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Account {
    @Id
    private String id;
    private BigDecimal balance = BigDecimal.ZERO.setScale(2);

    public Account() {
    } // <- obrigatório

    public Account(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal b) {
        this.balance = b;
    }
}
