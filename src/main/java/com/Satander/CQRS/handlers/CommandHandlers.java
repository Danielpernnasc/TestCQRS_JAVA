package com.Satander.CQRS.handlers;

import java.util.Map;
import java.util.UUID;

import com.Satander.CQRS.Db;
import com.Satander.CQRS.HttpUtil;
import com.Satander.CQRS.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

import com.fasterxml.jackson.core.type.TypeReference;

public class CommandHandlers {

    public static boolean auth(HttpExchange ex){
        var h = ex.getRequestHeaders().getFirst("Authorization");
        if(h == null || !h.startsWith("Bearer")){
            return false;
        }
        try {
            JwtUtil.subject(h.substring(7));
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static void createTransaction(HttpExchange ex) throws Exception {
        if(!auth(ex)){
            HttpUtil.json(ex, 401, Map.of("error", "unauthorized"));
            return;
        }
       
        var map = HttpUtil.M.readValue(HttpUtil.body(ex),
        new TypeReference<Map<String, Object>>() {});
        String accountId = (String) map.get("accountId");
        String type = (String)map.get("type");
        BigDecimal amount = new BigDecimal(map.get("amount").toString());
        if(accountId == null || type == null || amount == null){
            HttpUtil.json(ex, 400, Map.of("error", "dados"));
            return;
        }
        
        try (var c = Db.get()){
            c.setAutoCommit(false);
            try {
                try (var up = c.prepareStatement("MERGE INTO account (id, balance) KEY(id) VALUES(?, COALESCE((SELECT balance FROM account WHERE id=?), 0))")) {
                    up.setString(1, accountId);
                    up.setString(2, accountId);
                    up.executeUpdate();

                }
                BigDecimal saldo;
                try (var ps = c.prepareStatement("SELECT balance FROM account WHERE id=?")){
                    ps.setString(1, accountId);
                    try (var rs = ps.executeQuery()){
                        rs.next();
                        saldo = rs.getBigDecimal(1);
                    }

                }
                BigDecimal novo = type.equals("CREDIT") ? 
                saldo.add(amount) :
                saldo.subtract(amount);
                try (var ps=c.prepareStatement("UPDATE account SET balance=? WHERE id=?")){
                    ps.setBigDecimal(1, novo);
                    ps.setString(2, accountId);
                    ps.executeUpdate();
                }
                var txId = UUID.randomUUID().toString();
                    try (var ps = c.prepareStatement("INSERT INTO tx(id, account_id, type, created_at) VALUES(?,?,?,?,?)")) {
                        ps.setString(1, txId);
                        ps.setString(2, accountId);
                        ps.setBigDecimal(3, amount.setScale(2));
                        ps.setString(4, type);
                        ps.setTimestamp(5, Timestamp.from(Instant.now()));
                        ps.executeUpdate();
                    }
                    c.commit();
                } catch (Exception e) {
                    c.rollback();
                    throw e;
                }
            }
        }
    }


