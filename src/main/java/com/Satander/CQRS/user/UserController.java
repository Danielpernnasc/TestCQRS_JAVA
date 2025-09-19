package com.Satander.CQRS.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.Satander.CQRS.security.JwtUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repo;
    private final UserService users;

    // 👉 leia do application.properties (o mesmo que o filtro usa)
    @Value("${jwt.secret}")
    private String secret;

    // horas (ex.: 6). NÃO milissegundos.
    @Value("${jwt.ttlHours:6}")
    private int ttlHours;

    public UserController(UserRepository repo, UserService users) {
        this.repo = repo;
        this.users = users;
    }

    public record RegisterReq(
            @NotBlank String fullName,
            @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos") String cpf,
            @NotBlank String login,
            @NotBlank String password) {
    }

    public record RegisterResp(Long userId, String fullName, String cpf, String cpfFormatado, String login) {
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResp> register(@RequestBody @Valid RegisterReq r) {
        Long id = users.register(r.fullName(), r.cpf(), r.login(), r.password()); // Ensure this is inside the UserController class
        var u = repo.findById(id).orElseThrow();
        return ResponseEntity.ok(new RegisterResp(
                u.getId(),
                u.getFullName(),
                u.getCpf(),
                maskCpf(u.getCpf()),
                u.getLogin()));
    }

    public record LoginReq(@NotBlank String login, @NotBlank String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginReq r) {
        var u = repo.findByLogin(r.login())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login/senha inválidos"));
        if (!users.check(u, r.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login/senha inválidos");
        }
        if (secret == null || secret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "JWT secret não configurado (jwt.secret)");
        }
        try {
            // 🔑 usa o MESMO segredo e TTL (em horas) do properties
            String token = JwtUtil.issue(u.getLogin(), secret, ttlHours);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao emitir token: " + e.getMessage());
        }
    }

    private static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
