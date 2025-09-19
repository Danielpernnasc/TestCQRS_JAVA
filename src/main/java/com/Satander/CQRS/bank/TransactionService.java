package com.Satander.CQRS.bank;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransactionService {
    private final AccountRepository accounts;
    private final TransactionRepository txs;

    public TransactionService(AccountRepository a, TransactionRepository t) {
        this.accounts = a;
        this.txs = t;
    }

    @Transactional
    public Transaction create(String accountId, BigDecimal amount, TransactionType type) {
        var acc = accounts.findById(accountId).orElse(new Account(accountId));
        BigDecimal balance = acc.getBalance();

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        if (type == TransactionType.CREDIT) {
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal debt = balance.abs().multiply(new BigDecimal("1.02")).setScale(2, RoundingMode.HALF_UP);
                if (amount.compareTo(debt) >= 0) {
                    balance = amount.subtract(debt);
                } else {
                    balance = debt.subtract(amount).negate();
                }
            } else {
                balance = balance.add(amount);
            }
        } else { // DEBIT
            balance = balance.subtract(amount);
        }

        acc.setBalance(balance.setScale(2, RoundingMode.HALF_UP));
        accounts.save(acc);

        var tx = new Transaction(accountId, amount, type);
        return txs.save(tx);
    }
}