package com.Satander.CQRS.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter  {
    @Value("${jwt.secret}") 
    private String jwtSecret;


   

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var http = (HttpServletRequest) request;
        var h = http.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer" )) {
            try{
                var sub = JwtUtil.parse(h.substring(7), jwtSecret).getSubject();
                Authentication auth = new UsernamePasswordAuthenticationToken(sub, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch (Exception ignored) {}
               
            }
            filterChain.doFilter(request, response);
        }
    }



