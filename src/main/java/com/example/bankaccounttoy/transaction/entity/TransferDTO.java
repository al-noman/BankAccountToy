package com.example.bankaccounttoy.transaction.entity;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Value
public class TransferDTO {
    String sourceAccountIban;

    @NotBlank
    String destinationAccountIban;

    @Positive
    BigDecimal amount;
}
