package com.Satander.CQRS.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.Santander.CQRS.bank.Account;
import com.Santander.CQRS.bank.AccountRepository;
import com.Santander.CQRS.bank.ReadModelProjector;
import com.Santander.CQRS.bank.Transaction;
import com.Santander.CQRS.bank.TransactionRepository;
import com.Santander.CQRS.bank.TransactionService;
import com.Santander.CQRS.user.User;
import com.Santander.CQRS.user.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    AccountRepository accounts;
    @Mock
    TransactionRepository txs;
    @Mock
    UserRepository users;
    @Mock
    ReadModelProjector projector;

    TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(accounts, txs, users, projector);
        ReflectionTestUtils.setField(service, "negativeRate", new BigDecimal("0.02"));
    }

    @Test
    void deposito_quitaSaldoNegativoComJuros() {
        var user = new User("Maria Souza", "14587080047", "maria", "hash");
        var acc = new Account();
        acc.setUser(user);
        acc.setBalance(new BigDecimal("-100.00"));

        when(users.findByLogin("maria")).thenReturn(Optional.of(user));
        when(accounts.findByUser(user)).thenReturn(Optional.of(acc));
        when(txs.findByAccountOrderByTimestampDesc(acc)).thenReturn(List.of());

        var tx = service.deposit("maria", new BigDecimal("200.00"));

        assertThat(tx.getBalanceAfter()).isEqualByComparingTo("98.00");
        assertThat(acc.getBalance()).isEqualByComparingTo("98.00");

        verify(accounts).save(acc);
        verify(txs).save(any(Transaction.class));
        verify(projector).project(eq(acc), anyList());
    }

    @Test
    void pagamento_deixaContaNegativaSeNaoHaSaldo() {
        var user = new User("Maria Souza", "14587080047", "maria", "hash");
        var acc = new Account();
        acc.setUser(user);
        acc.setBalance(new BigDecimal("100.00"));

        when(users.findByLogin("maria")).thenReturn(Optional.of(user));
        when(accounts.findByUser(user)).thenReturn(Optional.of(acc));
        when(txs.findByAccountOrderByTimestampDesc(acc)).thenReturn(List.of());

        var tx = service.pay("maria", new BigDecimal("200.00"));

        assertThat(tx.getBalanceAfter()).isEqualByComparingTo("-100.00");
        assertThat(acc.getBalance()).isEqualByComparingTo("-100.00");
    }
}