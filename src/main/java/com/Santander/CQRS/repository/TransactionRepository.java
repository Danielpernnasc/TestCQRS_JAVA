package com.Santander.CQRS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.Santander.CQRS.model.Transaction;
import com.Santander.CQRS.model.Account;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountOrderByTimestampDesc(Account account);
}
