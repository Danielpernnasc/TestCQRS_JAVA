package com.Satander.CQRS.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {

    private static byte[] safeDecode(String s) {
        if (s == null)
            throw new IllegalArgumentException("JWT secret vazio");
        // 1) tenta Base64 clássico
        try {
            return Decoders.BASE64.decode(s);
        } catch (Exception ignored) {
        }
        // 2) tenta Base64URL (aceita '-' e '_')
        try {
            return Decoders.BASE64URL.decode(s);
        } catch (Exception ignored) {
        }
        // 3) usa bytes do texto (dev), e se for curto, “esticamos” com SHA-256
        byte[] raw = s.getBytes(StandardCharsets.UTF_8);
        if (raw.length < 32) { // HS256 precisa de >= 256 bits
            try {
                raw = MessageDigest.getInstance("SHA-256").digest(raw);
            } catch (Exception e) {
                throw new IllegalArgumentException("Não foi possível derivar chave JWT", e);
            }
        }
        return raw;
    }

    private static Key deriveKey(String secret) {
        byte[] keyBytes = safeDecode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String issue(String login, String secret, int ttlHours) {
        Key key = deriveKey(secret);
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlHours * 3600L);
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String parse(String token, String secret) {
        return getClaims(token, secret).getSubject();
    }

    public static Claims getClaims(String token, String secret) {
        Key key = deriveKey(secret);
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
}
