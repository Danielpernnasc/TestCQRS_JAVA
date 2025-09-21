package com.Santander.CQRS.bank;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Santander.CQRS.user.User;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(User user);
}
