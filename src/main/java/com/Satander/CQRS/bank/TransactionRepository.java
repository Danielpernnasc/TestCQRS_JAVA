package com.Satander.CQRS.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Satander.CQRS.bank.Transaction; // Ensure this import matches the actual location of the Transaction class

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}