package com.Satander.CQRS.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReadModelProjector {
    private final ReadModelCache cache;
    private final ObjectMapper mapper = new ObjectMapper();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public ReadModelProjector(ReadModelCache cache) {
        this.cache = cache;
    }

    public void project(Account acc, List<Transaction> txs) {
        ObjectNode root = mapper.createObjectNode();

        var cliente = root.putObject("Cliente");
        var u = acc.getUser();
        String cpf = u != null ? u.getCpf() : null;
        cliente.put("nome", u != null ? u.getFullName() : null);
        cliente.put("cpf", cpf);
        cliente.put("cpfFormatado", cpfMask(cpf));

        root.put("SaldoTotal", acc.getBalance().setScale(2).toString());

        ArrayNode hist = root.putArray("Historico");
        for (var t : txs) {
            var item = hist.addObject();
            item.put("type", t.getType() == TransactionType.DEPOSIT ? "deposito" : "saque");
            item.put("valor", t.getAmount().setScale(2).toString());
            item.put("data", t.getTimestamp().format(fmt));
            item.put("saldoApos", t.getBalanceAfter().setScale(2).toString());
        }
        cache.put(acc.getId(), root.toString());
    }

    private static String cpfMask(String cpf) {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
