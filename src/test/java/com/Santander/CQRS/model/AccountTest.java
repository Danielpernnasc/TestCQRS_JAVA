package com.Santander.CQRS.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;


class AccountTest {

    @Test
    void testSetAndGetId() {
        Account account = new Account();
        assertNull(account.getId(), "Initial ID should be null");
    }

    @Test
    void testSetId() {
        Account account = new Account();
        Long id = 1L;
        account.setId(id);
        assertEquals(id, account.getId(), "ID should be set correctly");
    }

    @Test
    void testSetAndGetUser() {
        Account account = new Account();
        User user = new User("John", "12345678901", "john", "passwordHash");
        account.setUser(user);
        assertEquals(user, account.getUser(), "User should be set correctly");
    }

    @Test
    void testSetAndGetBalance() {
        Account account = new Account();
        BigDecimal balance = new BigDecimal("150.50");
        account.setBalance(balance);
        assertEquals(balance, account.getBalance(), "Balance should be set correctly");
    }

    @Test
    void testSetBalanceNull() {
        Account account = new Account();
        account.setBalance(null);
        assertEquals(BigDecimal.ZERO.setScale(2), account.getBalance(), "Balance should default to 0.00 when set to null");
    }

    @Test
    void testDefaultBalance() {
        Account account = new Account();
        assertEquals(BigDecimal.ZERO.setScale(2), account.getBalance(), "Default balance should be 0.00");
    }
}