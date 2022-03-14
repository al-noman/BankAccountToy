package com.example.bankaccounttoy.bankaccount.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "BANK_ACCOUNT")
public class BankAccountEntity implements Serializable {
    @Id
    private String iban;

    @Enumerated(EnumType.STRING)
    private BankAccountType bankAccountType;

    private BigDecimal balance;

    private boolean locked;
}
