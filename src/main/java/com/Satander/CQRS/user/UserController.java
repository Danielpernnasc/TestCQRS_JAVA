package com.Satander.CQRS.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;

import com.Satander.CQRS.common.CpfValidator;
import com.Satander.CQRS.security.JwtUtil;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repo;
    private final UserService users;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.ttlHours:6}")
    private int ttl;

    public UserController(UserRepository repo, UserService users) {
        this.repo = repo;
        this.users = users;
    }

    public record RegisterReq(String fullName, String cpf, String login, String password) {
    }

    public record LoginReq(String login, String password) {
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterReq r) {
        if (!CpfValidator.isValid(r.cpf()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        var user = users.create(r.fullName(), r.cpf(), r.login(), r.password());
        return ResponseEntity.ok(Map.of("userId", user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginReq r) {
        var u = repo.findByLogin(r.login())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!users.check(u, r.password()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String token = JwtUtil.issue(u.getLogin(), secret, ttl);
        return ResponseEntity.ok(Map.of("token", token));
    }
}