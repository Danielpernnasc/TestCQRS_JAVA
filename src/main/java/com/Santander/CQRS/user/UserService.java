package com.Santander.CQRS.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Santander.CQRS.common.CpfValidator;

// com/Satander/CQRS/user/UserService.java
@Service
public class UserService {
  private final UserRepository repo;
  private final PasswordEncoder enc;

  public UserService(UserRepository r, PasswordEncoder e) { this.repo = r; this.enc = e; }
  
  public boolean check(User user, String rawPassword) {
      return enc.matches(rawPassword, user.getPasswordHash());
  }

  @Transactional
  public Long register(String fullName, String cpf, String login, String rawPassword) {
    String cpfLimpo = cpf.replaceAll("\\D", "");         // só dígitos
    if (!CpfValidator.isValid(cpfLimpo))                 // <-- valida DV
      throw new IllegalArgumentException("CPF inválido");
    if (repo.existsByLogin(login)) throw new IllegalStateException("Login já cadastrado");
    if (repo.existsByCpf(cpfLimpo)) throw new IllegalStateException("CPF já cadastrado");

    var u = new User(fullName, cpfLimpo, login, enc.encode(rawPassword));
    return repo.save(u).getId();
  }

  // (opcional) se ainda existir:
  @Transactional
  public User create(String fullName, String cpf, String login, String rawPassword) {
    Long id = register(fullName, cpf, login, rawPassword);   // delega para register (com validação)
    return repo.findById(id).orElseThrow();
  }
}


