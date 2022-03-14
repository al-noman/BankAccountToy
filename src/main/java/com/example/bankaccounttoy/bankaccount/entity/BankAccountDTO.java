package com.example.bankaccounttoy.bankaccount.entity;

import com.example.bankaccounttoy.bankaccount.control.ValidIBAN;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static java.math.BigDecimal.ZERO;

@Data
public class BankAccountDTO {
    @ValidIBAN
    private String iban;

    @NotNull
    private BankAccountType bankAccountType;

    private BigDecimal balance = ZERO;

    @JsonProperty(access = READ_ONLY)
    private boolean locked;
}
