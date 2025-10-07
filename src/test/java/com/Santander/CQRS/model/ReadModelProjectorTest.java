package com.Santander.CQRS.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

class ReadModelProjectorTest {

    private ReadModelCache cache;
    private ReadModelProjector projector;

    @BeforeEach
    void setUp() {
        cache = mock(ReadModelCache.class);
        projector = new ReadModelProjector(cache);
    }

    @Test
    void testProjectWithValidAccount() {
        User user = new User();
        user.setFullName("João Silva");
        user.setCpf("12345678901");

        Account account = new Account();
        account.setUser(user);
        account.setBalance(new BigDecimal("100.00"));
        account.setId(1L);

        Transaction tx = new Transaction();
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(new BigDecimal("50.00"));
        tx.setTimestamp(java.time.LocalDateTime.now());
        tx.setBalanceAfter(new BigDecimal("150.00"));

        projector.project(account, List.of(tx));

        verify(cache, times(1)).put(eq(1L), anyString());
    }

    @Test
    void testProjectWithNullAccount() {
        projector.project(null, List.of());
        verify(cache, never()).put(anyLong(), anyString());
    }
}