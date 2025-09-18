package com.Satander.CQRS.handlers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;

import com.Satander.CQRS.Cache;
import com.Satander.CQRS.Db;
import com.Satander.CQRS.HttpUtil;
import com.sun.net.httpserver.HttpExchange;

import com.fasterxml.jackson.core.type.TypeReference;

public class QueryHandlers {
    public static void balance(HttpExchange ex, String accountId, int limit) throws Exception {
     
        Cache cache = new Cache();
        String cBal = cache.getBalance(accountId);
        var cHist = Cache.getHist(accountId, limit);

        if(cBal != null && !cHist.isEmpty()){
            var hist = new ArrayList<Map<String, Object>>();
            for (var s: cHist) hist.add(HttpUtil.M.readValue(s, new TypeReference<Map<String, Object>>(){}));
            HttpUtil.json(ex, 200, Map.of("SaldoTotal", cBal, "Historico", hist));
            return;
        }

        try (var c = Db.get()){
            BigDecimal saldo = BigDecimal.ZERO;
            var hist = new ArrayList<Map<String, Object>>();
            try (var ps = c.prepareStatement("SELECT balance FROM account WHERE id=?")){
                ps.setString(1, accountId); 
                ps.setInt(2, limit);
                try (var rs = ps.executeQuery()){
                    while (rs.next()){
                        String type = rs.getString(2);
                        var amount = rs.getBigDecimal(1);
                        var time = rs.getTimestamp(3).toInstant();
                        hist.add(Map.of(
                            "type", type.equals("CREDIT") ? "deposito" : "saque",
                            "valor", amount.setScale(2).toPlainString(),
                            "data", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(time)
                        ));
                    }}
            }
            HttpUtil.json(ex, 200, Map.of("SaldoTotal", saldo.setScale(2).toPlainString(), "Historico", hist));
        }
    }

}
