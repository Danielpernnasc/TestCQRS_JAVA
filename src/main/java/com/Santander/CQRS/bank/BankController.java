package com.Santander.CQRS.bank;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.Santander.CQRS.user.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bank")
public class BankController {

        private final TransactionService svc;
        private final TransactionRepository txs;
        private final AccountRepository accounts;
        private final UserRepository users;
        private final ReadModelCache cache;

        public BankController(TransactionService svc,
                        TransactionRepository txs,
                        AccountRepository accounts,
                        UserRepository users,
                        ReadModelCache cache) {
                this.svc = svc;
                this.txs = txs;
                this.accounts = accounts;
                this.users = users;
                this.cache = cache;
        }

        public record TxRequest(String accountId, String type, BigDecimal amount) {
        }

        // ---------- Criação genérica (deposito/pagamento) ----------
        @PostMapping("/transactions")
        public ResponseEntity<?> create(@RequestBody TxRequest r, Authentication auth) {
                // 0) valida auth
                if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente ou inválido");
                }

                // 1) valida body
                if (r == null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body obrigatório");
                if (r.type() == null || r.type().isBlank())
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type obrigatório");
                if (r.amount() == null || r.amount().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) <= 0)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount inválido");

                // 2) normaliza o tipo
                String norm = r.type().trim().toUpperCase();
                TransactionType tt = switch (norm) {
                        case "CREDIT", "DEPOSIT", "DEPOSITO" -> TransactionType.DEPOSIT;
                        case "DEBIT", "PAYMENT", "PAGAMENTO", "SAQUE" -> TransactionType.PAYMENT;
                        default ->
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido: " + r.type());
                };

                try {
                        var tx = svc.create(r.accountId(), r.amount(), tt, auth.getName());
                        return ResponseEntity.ok(Map.of(
                                        "transactionId", tx.getId(),
                                        "type", tt.name(),
                                        "amount", tx.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                                        "saldo",
                                        tx.getBalanceAfter().setScale(2, RoundingMode.HALF_UP).toPlainString()));
                } catch (NumberFormatException nfe) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "accountId inválido (use número)");
                } catch (IllegalArgumentException iae) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, iae.getMessage());
                } catch (DataIntegrityViolationException dive) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                        "Violação de integridade: " + dive.getMostSpecificCause().getMessage());
                } catch (ResponseStatusException rse) {
                        throw rse;
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Falha ao processar transação");
                }
        }

        // ---------- JSON do histórico + saldo ----------
        @GetMapping("/me/summary")
        public ResponseEntity<?> summary(Authentication auth,
                        @RequestParam(defaultValue = "20") int limit) {
                if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente ou inválido");
                }

                var user = users.findByLogin(auth.getName())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                                "Usuário não encontrado"));

                // garante que exista a conta
                var acc = accounts.findByUser(user).orElseGet(() -> {
                        var a = new Account();
                        a.setUser(user);
                        a.setBalance(BigDecimal.ZERO);
                        return accounts.save(a);
                });

                // 1) tenta usar o read-model em cache (CQRS)
                try {
                        String cached = cache.get(acc.getId());
                        if (cached != null) {
                                return ResponseEntity.ok()
                                                .header("Content-Type", "application/json")
                                                .body(cached);
                        }
                } catch (Exception ignore) {
                        /* se cache não estiver pronto, monta ao vivo */ }

                // 2) monta ao vivo
                var fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                List<Map<String, String>> historico = txs.findByAccountOrderByTimestampDesc(acc).stream()
                                .limit(limit)
                                .map(t -> Map.of(
                                                "type", t.getType() == TransactionType.DEPOSIT ? "deposito" : "saque",
                                                "valor",
                                                t.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                                                "data", t.getTimestamp().format(fmt)))
                                .toList();

                String saldo = acc.getBalance().setScale(2, RoundingMode.HALF_UP).toPlainString();

                return ResponseEntity.ok(Map.of(
                                "SaldoTotal", saldo,
                                "Historico", historico));
        }
}
