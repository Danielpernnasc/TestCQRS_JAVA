package com.Satander.CQRS.bank;

import com.Satander.CQRS.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final AccountRepository accounts;
    private final TransactionRepository txs;
    private final UserRepository users;
    private final ReadModelProjector projector;

    @Value("${bank.negativeRate:0.02}")
    private BigDecimal negativeRate;

    public TransactionService(AccountRepository a, TransactionRepository t, UserRepository u, ReadModelProjector p) {
        this.accounts = a;
        this.txs = t;
        this.users = u;
        this.projector = p;
    }

    private static BigDecimal s2(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }

    private Account getOrCreateByLogin(String login) {
        var user = users.findByLogin(login).orElseThrow();
        return accounts.findByUser(user).orElseGet(() -> {
            var acc = new Account();
            acc.setUser(user);
            acc.setBalance(BigDecimal.ZERO);
            return accounts.save(acc);
        });
    }

    private BigDecimal afterDeposit(BigDecimal current, BigDecimal dep) {
        current = s2(current);
        dep = s2(dep);
        if (current.signum() < 0) {
            var debt = current.abs();
            var withInterest = debt.multiply(BigDecimal.ONE.add(negativeRate)).setScale(2, RoundingMode.HALF_UP);
            return dep.compareTo(withInterest) >= 0
                    ? dep.subtract(withInterest).setScale(2, RoundingMode.HALF_UP)
                    : withInterest.subtract(dep).negate().setScale(2, RoundingMode.HALF_UP);
        }
        return current.add(dep).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public Transaction deposit(String login, BigDecimal amount) {
        amount = s2(amount);
        var acc = getOrCreateByLogin(login);
        var newBal = afterDeposit(acc.getBalance(), amount);
        acc.setBalance(newBal);
        accounts.save(acc);

        var tx = new Transaction();
        tx.setAccount(acc);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setBalanceAfter(newBal);
        tx.setTimestamp(LocalDateTime.now());
        txs.save(tx);

        projector.project(acc, txs.findByAccountOrderByTimestampDesc(acc));
        return tx;
    }

    @Transactional
    public Transaction pay(String login, BigDecimal amount) {
        amount = s2(amount);
        var acc = getOrCreateByLogin(login);
        var newBal = acc.getBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP);
        acc.setBalance(newBal);
        accounts.save(acc);

        var tx = new Transaction();
        tx.setAccount(acc);
        tx.setType(TransactionType.PAYMENT);
        tx.setAmount(amount);
        tx.setBalanceAfter(newBal);
        tx.setTimestamp(LocalDateTime.now());
        txs.save(tx);

        projector.project(acc, txs.findByAccountOrderByTimestampDesc(acc));
        return tx;
    }

    @Transactional
    public Transaction create(String accountId, BigDecimal amount, TransactionType type, String login) {
        if (type == null)
            throw new IllegalArgumentException("Tipo de transação obrigatório");
        if (accountId != null && !accountId.isBlank()) {
            // delega pelo login do dono da conta para reaproveitar a regra/fluxo
            Long id = Long.parseLong(accountId);
            var acc = accounts.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found"));
            var ownerLogin = acc.getUser().getLogin();
            return (type == TransactionType.DEPOSIT) ? deposit(ownerLogin, amount) : pay(ownerLogin, amount);
        }
        return (type == TransactionType.DEPOSIT) ? deposit(login, amount) : pay(login, amount);
    }
}
