package com.Satander.CQRS;

import java.sql.*;

    public class Db {
        public static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        public static Connection get() throws Exception { return DriverManager.getConnection(URL, "sa", ""); }
            public static void init() throws Exception {
            try (var c = get(); var st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS app_user (id IDENTITY PRIMARY KEY, full_name VARCHAR(120), cpf VARCHAR(14) UNIQUE, login VARCHAR(60) UNIQUE, password_hash VARCHAR(100));");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS account (id VARCHAR(60) PRIMARY KEY, balance DECIMAL(19,2) DEFAULT 0);");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS tx (id VARCHAR(40) PRIMARY KEY, account_id VARCHAR(60), amount DECIMAL(19,2), type VARCHAR(10), created_at TIMESTAMP);");
            }
        }
    }
