package com.Satander.CQRS.bank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/bank")
public class BankController {
    private final TransactionService svc;
    private final AccountRepository accounts;
    private final TransactionRepository txs;

    public BankController(TransactionService s, AccountRepository a, TransactionRepository t) {
        this.svc = s;
        this.accounts = a;
        this.txs = t;
    }

    record TxRequest(String accountId, TransactionType type, java.math.BigDecimal amount) {
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> create(@RequestBody TxRequest r) {
        var tx = svc.create(r.accountId(), r.amount(), r.type());
        return ResponseEntity.ok(Map.of("transactionId", tx.getId()));
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<?> balance(@PathVariable String id) {
        var acc = accounts.findById(id).orElse(new Account(id));
        var fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault());

        var historico = txs.findAll().stream()
                .filter(t -> t.getAccountId().equals(id))
                .map(t -> Map.of(
                        "type", t.getType().equals("CREDIT") ? "deposito" : "saque",
                        "valor", t.getAmount().toPlainString(),
                        "data", fmt.format(t.getCreatedAt())))
                .toList();

        return ResponseEntity.ok(Map.of(
                "SaldoTotal", acc.getBalance().toPlainString(),
                "Historico", historico));
    }
}
