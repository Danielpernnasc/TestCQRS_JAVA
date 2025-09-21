package com.Satander.CQRS.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.Santander.CQRS.bank.Account;
import com.Santander.CQRS.bank.AccountRepository;
import com.Santander.CQRS.bank.BankController;
import com.Santander.CQRS.bank.ReadModelCache;
import com.Santander.CQRS.bank.Transaction;
import com.Santander.CQRS.bank.TransactionRepository;
import com.Santander.CQRS.bank.TransactionService;
import com.Santander.CQRS.bank.TransactionType;
import com.Santander.CQRS.user.User;
import com.Santander.CQRS.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BankControllerStandaloneTest {

    @Mock
    TransactionService transactionService;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ReadModelCache readModelCache;

    @InjectMocks
    BankController controller;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void summary_retornaSaldoEHistorico() throws Exception {
        var user = new User("Maria", "14587080047", "maria", "hash");
        var acc = new Account();
        acc.setUser(user);
        acc.setBalance(new BigDecimal("98.00"));
        // evita NPE no cache (id nulo)
        ReflectionTestUtils.setField(acc, "id", 1L);

        var t1 = new Transaction();
        t1.setAccount(acc);
        t1.setType(TransactionType.DEPOSIT);
        t1.setAmount(new BigDecimal("200.00"));
        t1.setBalanceAfter(new BigDecimal("98.00"));
        t1.setTimestamp(LocalDateTime.now());

        when(userRepository.findByLogin("maria")).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(Optional.of(acc));
        when(transactionRepository.findByAccountOrderByTimestampDesc(acc)).thenReturn(List.of(t1));
        when(readModelCache.get(1L)).thenReturn(null);

        mvc.perform(get("/bank/me/summary?limit=10")
                .principal(new TestingAuthenticationToken("maria", null))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SaldoTotal").value("98.00"))
                .andExpect(jsonPath("$.Historico[0].type").value("deposito"))
                .andExpect(jsonPath("$.Historico[0].valor").value("200.00"));
    }
}
