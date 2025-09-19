package com.Satander.CQRS.bank;

import com.Satander.CQRS.user.User;
import com.Satander.CQRS.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ReadModelCache readModelCache;

    @Test
    void summary_retornaSaldoEHistorico() throws Exception {
        // Arrange
        var user = new User("Maria", "14587080047", "maria", "hash");
        var acc = new Account();
        acc.setUser(user);
        acc.setBalance(new BigDecimal("98.00"));

        var t1 = new Transaction();
        t1.setAccount(acc);
        t1.setType(TransactionType.DEPOSIT);
        t1.setAmount(new BigDecimal("200.00"));
        t1.setBalanceAfter(new BigDecimal("98.00"));
        t1.setTimestamp(LocalDateTime.now());

        when(userRepository.findByLogin("maria")).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(Optional.of(acc));
        when(transactionRepository.findByAccountOrderByTimestampDesc(acc)).thenReturn(List.of(t1));
        when(readModelCache.get(acc.getId())).thenReturn(null);

        // Act & Assert
        mvc.perform(get("/bank/me/summary?limit=10")
                .principal(new TestingAuthenticationToken("maria", null))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SaldoTotal").value("98.00"))
                .andExpect(jsonPath("$.Historico[0].type").value("deposito"))
                .andExpect(jsonPath("$.Historico[0].valor").value("200.00"));
    }
}