package com.Satander.CQRS.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByCpf(String cpf);

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);
}
