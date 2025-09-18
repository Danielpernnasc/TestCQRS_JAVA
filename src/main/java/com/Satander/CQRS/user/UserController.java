package com.Satander.CQRS.user;

import com.Satander.CQRS.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService users;
    @Autowired
    private UserRepository repo;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.ttlHours:6}")
    private int ttl;

    public UserController() {
    } // <- resolve “No default constructor found”

    public record RegisterReq(String fullName, String cpf, String login, String password) {
    }

    public record LoginReq(String login, String password) {
    }
    public record LoginRes(String token) {
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq r) {
        var id = users.register(r.fullName(), r.cpf(), r.login(), r.password());
        return ResponseEntity.ok(Map.of("userId", id));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq r) {
        var u = repo.findByLogin(r.login()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!users.check(u, r.password()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        String token = JwtUtil.issue(u.getLogin(), secret, ttl);
        System.out.println("Token gerado: " + token); // <-- Adicione esta linha
        return ResponseEntity.ok(new LoginRes(token));
    }
}
