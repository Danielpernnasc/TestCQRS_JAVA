package com.Santander.CQRS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.Santander.CQRS.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByCpf(String cpf);

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);
}
