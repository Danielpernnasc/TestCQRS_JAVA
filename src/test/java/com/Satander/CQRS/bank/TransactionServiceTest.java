package com.Satander.CQRS.bank;

import com.Satander.CQRS.user.User;
import com.Satander.CQRS.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private AccountRepository accounts;
    private TransactionRepository txs;
    private UserRepository users;
    private ReadModelProjector projector;
    private TransactionService service;

    @BeforeEach
    void setup() {
        accounts = mock(AccountRepository.class);
        txs = mock(TransactionRepository.class);
        users = mock(UserRepository.class);
        projector = mock(ReadModelProjector.class);
        service = new TransactionService(accounts, txs, users, projector);
    }

    @Test
    void deposit_success() {
        User user = new User("Nome", "12345678909", "login", "hash");
        Account acc = new Account();
        acc.setUser(user);
        acc.setBalance(BigDecimal.ZERO);

        when(users.findByLogin("login")).thenReturn(Optional.of(user));
        when(accounts.findByUser(user)).thenReturn(Optional.of(acc));
        when(accounts.save(any(Account.class))).thenReturn(acc);

        Transaction tx = service.deposit("login", BigDecimal.valueOf(100));
        assertThat(tx.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(tx.getAmount()).isEqualByComparingTo("100.00");
        verify(accounts).save(acc);
        verify(txs).save(any(Transaction.class));
        verify(projector).project(eq(acc), any());
    }

    @Test
    void pay_success() {
        User user = new User("Nome", "12345678909", "login", "hash");
        Account acc = new Account();
        acc.setUser(user);
        acc.setBalance(BigDecimal.valueOf(200));

        when(users.findByLogin("login")).thenReturn(Optional.of(user));
        when(accounts.findByUser(user)).thenReturn(Optional.of(acc));
        when(accounts.save(any(Account.class))).thenReturn(acc);

        Transaction tx = service.pay("login", BigDecimal.valueOf(50));
        assertThat(tx.getType()).isEqualTo(TransactionType.PAYMENT);
        assertThat(tx.getAmount()).isEqualByComparingTo("50.00");
        verify(accounts).save(acc);
        verify(txs).save(any(Transaction.class));
        verify(projector).project(eq(acc), any());
    }

    @Test
    void create_withAccountId_deposit() {
        User user = new User("Nome", "12345678909", "login", "hash");
        Account acc = new Account();
        acc.setUser(user);
        acc.setBalance(BigDecimal.ZERO);

        when(accounts.findById(1L)).thenReturn(Optional.of(acc));
        when(accounts.save(any(Account.class))).thenReturn(acc);

        TransactionService spyService = spy(service);
        doReturn(new Transaction()).when(spyService).deposit(anyString(), any());
        doReturn(new Transaction()).when(spyService).pay(anyString(), any());

        Transaction tx = spyService.create("1", BigDecimal.valueOf(10), TransactionType.DEPOSIT, "login");
        assertThat(tx).isNotNull();
    }

    @Test
    void create_withAccountId_pay() {
        User user = new User("Nome", "12345678909", "login", "hash");
        Account acc = new Account();
        acc.setUser(user);
        acc.setBalance(BigDecimal.ZERO);

        when(accounts.findById(1L)).thenReturn(Optional.of(acc));
        when(accounts.save(any(Account.class))).thenReturn(acc);

        TransactionService spyService = spy(service);
        doReturn(new Transaction()).when(spyService).deposit(anyString(), any());
        doReturn(new Transaction()).when(spyService).pay(anyString(), any());

        Transaction tx = spyService.create("1", BigDecimal.valueOf(10), TransactionType.PAYMENT, "login");
        assertThat(tx).isNotNull();
    }

    @Test
    void create_withoutAccountId_deposit() {
        TransactionService spyService = spy(service);
        doReturn(new Transaction()).when(spyService).deposit(anyString(), any());
        doReturn(new Transaction()).when(spyService).pay(anyString(), any());

        Transaction tx = spyService.create(null, BigDecimal.valueOf(10), TransactionType.DEPOSIT, "login");
        assertThat(tx).isNotNull();
    }

    @Test
    void create_throwsException_whenTypeIsNull() {
        assertThatThrownBy(() -> service.create("1", BigDecimal.TEN, null, "login"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de transação obrigatório");
    }

    @Test
    void create_throwsException_whenAccountNotFound() {
        when(accounts.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create("99", BigDecimal.TEN, TransactionType.DEPOSIT, "login"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found");
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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