package com.Satander.CQRS.security;

import java.sql.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;



public class JwtUtil {
    private static final String ISSUER = "cqrs-demo";
    private static final String SECRET = System.getenv().getOrDefault("JWT_SECRET", "dev-secret-change");
    private static final Algorithm ALG = Algorithm.HMAC256(SECRET);
    private static final long EXP_MS = 1000L * 60 * 60 * 6; // 6h
 
    private JwtUtil() {}


    public static String issue(String login) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(login)
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + EXP_MS))
                .sign(ALG);
    }

    public static DecodedJWT parse(String token, String secret){
        Algorithm alg = Algorithm.HMAC256(secret);
        return JWT.require(alg)
                .withIssuer(ISSUER)
                .build()
                .verify(token);
    }

    public static String subject(String token) {
        try {
            JWTVerifier verifier = JWT.require(ALG).withIssuer(ISSUER).build();
            return verifier.verify(token).getSubject();
        } catch (JWTVerificationException e) {
            return null; 
        }
    }

}
