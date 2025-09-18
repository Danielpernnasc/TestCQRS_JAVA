package com.Satander.CQRS;

import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    public static final ObjectMapper M = new ObjectMapper();
    public static String body(HttpExchange ex) throws IOException { return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); }
    public static void json(HttpExchange ex, int code, Object obj) throws IOException {
        var bytes = M.writeValueAsBytes(obj);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.sendResponseHeaders(code, bytes.length);
        try (var os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
    public static void options(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.sendResponseHeaders(204, -1);
    }

}
