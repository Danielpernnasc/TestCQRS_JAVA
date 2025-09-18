package com.Satander.CQRS.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.Satander.CQRS.security.JwtUtil;


import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/users")

public class UserController {
   private final UserService users;
  private final UserRepository repo;

  private final String secret;
  private final int ttl;

  public UserController(UserService u, UserRepository r, 
                        @Value("${jwt.secret}") String secret, 
                        @Value("${jwt.ttlHours:6}") int ttl) {
      this.users = u;
      this.repo = r;
      this.secret = secret;
      this.ttl = ttl;
  }
    

    public UserController(UserService u, UserRepository r) {
        this.users = u;
        this.repo = r;
        this.secret = "defaultSecret"; // Provide a default value for the secret
        this.ttl = 6; // Provide a default value for the TTL
    }

    public record RegisterReq(String fullName, String cpf, String login, String password){

    }
    public record LoginReq(String login, String password){

    }
    public record LoginRes(String toke){

    }


// Refatorado para remover o método duplicado de register e corrigir os nomes dos métodos de acesso
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterReq r) {
    var id = users.register(r.fullName(), r.cpf(), r.login(), r.password());
    return ResponseEntity.ok(Map.of("userId", id));
}

@PostMapping("/login")
public ResponseEntity<LoginRes> login(@RequestBody LoginReq r) {
    var u = repo.findByLogin(r.login()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    String token = JwtUtil.issue(u.getLogin()); // Generate token using login
    return ResponseEntity.ok(new LoginRes(token));
}
   

    


}
