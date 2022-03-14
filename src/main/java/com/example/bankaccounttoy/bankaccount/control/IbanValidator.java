package com.example.bankaccounttoy.bankaccount.control;

import org.apache.commons.validator.routines.IBANValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<ValidIBAN, String> {
    private IBANValidator ibanValidator;
    public void initialize(ValidIBAN constraint) {
        this.ibanValidator = new IBANValidator();
    }

    public boolean isValid(String iban, ConstraintValidatorContext context) {
        return ibanValidator.isValid(iban);
    }
}
