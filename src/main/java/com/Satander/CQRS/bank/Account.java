package com.Satander.CQRS.bank;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Account {
    @Id
    private String accountId;
    private BigDecimal balance = BigDecimal.ZERO.setScale(2);

    public Account(String accountId) {

        this.accountId = accountId;

        this.balance = BigDecimal.ZERO; // Default balance

    

    }

 


    public BigDecimal getBalance() {

        return balance;

    }

    public void setBalance(BigDecimal balance) {

        this.balance = balance;

    }




}
