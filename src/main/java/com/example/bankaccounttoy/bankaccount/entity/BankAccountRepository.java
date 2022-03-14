package com.example.bankaccounttoy.bankaccount.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, String> {
    Optional<BankAccountEntity> findByIban(String iban);

    @Query("SELECT ba FROM BankAccountEntity ba WHERE ba.bankAccountType IN (:types)")
    List<BankAccountEntity> findByTypes(List<BankAccountType> types);
}
