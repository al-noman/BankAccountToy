package com.example.bankaccounttoy.transaction.entity;

import com.example.bankaccounttoy.bankaccount.entity.BankAccountEntity;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "TRANSACTION")
public class TransactionEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    private BankAccountEntity source;

    @ManyToOne
    private BankAccountEntity destination;

    private BigDecimal amount;
}
