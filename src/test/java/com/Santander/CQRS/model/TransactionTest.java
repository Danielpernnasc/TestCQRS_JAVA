package com.Santander.CQRS.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;






class TransactionTest {

    @Test
    void testTransactionGettersAndSetters() {
        Transaction transaction = new Transaction();
        Account account = new Account();
        TransactionType type = TransactionType.DEPOSIT;
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal balanceAfter = new BigDecimal("500.00");
        LocalDateTime timestamp = LocalDateTime.now();

        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setTimestamp(timestamp);

        assertEquals(account, transaction.getAccount());
        assertEquals(type, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(balanceAfter, transaction.getBalanceAfter());
        assertEquals(timestamp, transaction.getTimestamp());
    }

    @Test
    void testTransactionId() {
        Transaction transaction = new Transaction();
        Long id = 1L;
        transaction.setId(id);
        assertEquals(id, transaction.getId());
    }

    @Test
    void testNullAccount() {
        Transaction transaction = new Transaction();
        transaction.setAccount(null);
        assertNull(transaction.getAccount());
    }

    @Test
    void testNullType() {
        Transaction transaction = new Transaction();
        transaction.setType(null);
        assertNull(transaction.getType());
    }

    @Test
    void testNullAmount() {
        Transaction transaction = new Transaction();
        transaction.setAmount(null);
        assertNull(transaction.getAmount());
    }

    @Test
    void testNullBalanceAfter() {
        Transaction transaction = new Transaction();
        transaction.setBalanceAfter(null);
        assertNull(transaction.getBalanceAfter());
    }

    @Test
    void testNullTimestamp() {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(null);
        assertNull(transaction.getTimestamp());
    }

    @Test
    void testMultipleSetters() {
        Transaction transaction = new Transaction();
        Account account = new Account();
        transaction.setAccount(account);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setBalanceAfter(new BigDecimal("450.00"));
        transaction.setTimestamp(LocalDateTime.of(2024, 6, 1, 12, 0));
        assertEquals(account, transaction.getAccount());
        assertEquals(TransactionType.PAYMENT, transaction.getType());
        assertEquals(new BigDecimal("50.00"), transaction.getAmount());
        assertEquals(new BigDecimal("450.00"), transaction.getBalanceAfter());
        assertEquals(LocalDateTime.of(2024, 6, 1, 12, 0), transaction.getTimestamp());
    }
}