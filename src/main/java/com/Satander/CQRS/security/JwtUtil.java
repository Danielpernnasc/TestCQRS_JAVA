package com.Satander.CQRS.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static SecretKey key(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String issue(String subject, String secret, int ttlHours) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlHours * 3600_000L);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parse(String token, String secret) {
        return Jwts.parserBuilder().setSigningKey(key(secret)).build().parseClaimsJws(token);
    }
}
