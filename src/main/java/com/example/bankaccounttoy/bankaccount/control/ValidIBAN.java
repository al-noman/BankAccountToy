package com.example.bankaccounttoy.bankaccount.control;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIBAN {
    String message() default "Invalid IBAN number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
