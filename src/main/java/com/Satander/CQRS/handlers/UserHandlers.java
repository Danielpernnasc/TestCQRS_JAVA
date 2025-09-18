package com.Satander.CQRS.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.Satander.CQRS.CpfValidator;
import com.Satander.CQRS.Db;
import com.Satander.CQRS.HttpUtil;
import com.Satander.CQRS.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
// Removed unused or unresolved import


public class UserHandlers {
    public static void register(HttpExchange ex) throws Exception {
        var map = HttpUtil.M.readValue(HttpUtil.body(ex), new TypeReference<Map<String, String>>() {});
        String fullName = map.get("fullName"), cpf = map.get("cpf"), login = map.get("login"), password = map.get("password");
        
        if(fullName == null || cpf == null || login == null || password == null) {
            HttpUtil.json(ex, 400, Map.of("error", "Campos obrigatórios")); 
            return;
        }

    if(!CpfValidator.isValid(cpf)) {
        HttpUtil.json(ex, 422, Map.of("error", "CPF inválido"));
        return;
    }

    try(Connection c = Db.get()) {
        try (var ps = c.prepareStatement("SELECT 1 FROM app_user WHERE login=? OR cpf=?")) {
            ps.setString(1, login);
            ps.setString(2, cpf);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpUtil.json(ex, 409, Map.of("error", "Usuário já existe"));
                    return;
                }
            }
        }

        try (var ps = c.prepareStatement("INSERT INTO app_user(full_name,cpf,login,password_hash) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullName);
            ps.setString(2, cpf);
            ps.setString(3, login);
            ps.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.executeUpdate();
        }
    }
}

 public static void login(HttpExchange ex) throws Exception {
     var map = HttpUtil.M.readValue(HttpUtil.body(ex), new TypeReference<Map<String, String>>() {});
     String login = map.get("login"), password = map.get("password");
        try(
            var c = Db.get(); 
            var ps = c.prepareStatement("SELECT password_hash FROM app_user WHERE login=?")) {
                ps.setString(1, login);
                try(var rs = ps.executeQuery()) {
                    if(!rs.next()) {
                        HttpUtil.json(ex, 401, Map.of("error", "Credenciais inválidas"));
                        return;
                    }
                    
var hash = rs.getString(1);if(!BCrypt.checkpw(password,hash))
{
    HttpUtil.json(ex, 401, Map.of("error", "Credenciais"));
    return;
}
var token = JwtUtil.issue(login);HttpUtil.json(ex,200,Map.of("token",token));
                }
           
            }
        }
  
 }


          
       



