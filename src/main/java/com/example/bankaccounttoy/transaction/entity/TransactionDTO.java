package com.example.bankaccounttoy.transaction.entity;

import com.example.bankaccounttoy.bankaccount.entity.BankAccountDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
public class TransactionDTO {
    @JsonProperty(access = READ_ONLY)
    private UUID id;

    private BankAccountDTO source;

    @NotNull
    private BankAccountDTO destination;

    @Positive
    private BigDecimal amount;
}
