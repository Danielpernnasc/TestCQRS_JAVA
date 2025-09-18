package com.Satander.CQRS.user;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Satander.CQRS.common.CpfValidator;


@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder enc;

    public UserService(UserRepository r, PasswordEncoder e) {
        this.repo = r;
        this.enc = e;
    }

    public Long register(String fullName, String cpf, String login, String password) {
        if (!CpfValidator.isValid(cpf))
            throw new IllegalArgumentException("CPF inválido");
        if (repo.existsByLogin(login) || repo.existsByCpf(cpf))
            throw new IllegalStateException("Login/CPF já cadastrado");
        var u = new User(fullName, cpf, login, password);
        u.setFullName(fullName);
        u.setCpf(cpf);
        u.setLogin(login);
        u.setPasswordHash(enc.encode(password));
        return repo.save(u).getId();
    }

    public boolean check(User u, String raw) {
        return enc.matches(raw, u.getPassword());
    }
}
