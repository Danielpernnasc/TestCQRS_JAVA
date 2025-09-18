package com.Satander.CQRS.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String h = request.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) { // <— com espaço
            String token = h.substring(7);
            try {
                String sub = JwtUtil.parse(token, jwtSecret).getBody().getSubject();
                Authentication auth = new UsernamePasswordAuthenticationToken(sub, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
                /* token inválido → segue sem auth */ }
        }
        chain.doFilter(request, response);
    }
}
