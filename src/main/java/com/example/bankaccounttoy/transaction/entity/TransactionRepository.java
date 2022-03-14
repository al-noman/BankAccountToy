package com.example.bankaccounttoy.transaction.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT tx FROM TransactionEntity tx WHERE tx.source.iban=:iban OR tx.destination.iban=:iban")
    List<TransactionEntity> findAllByBankAccount(String iban);
}
