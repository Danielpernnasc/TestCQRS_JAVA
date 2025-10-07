
package com.Santander.CQRS.repository;
import com.Santander.CQRS.model.Account;
import com.Santander.CQRS.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;



public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(User user);
}
