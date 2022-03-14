package com.example.bankaccounttoy.exceptions;

import lombok.Value;

import java.util.Date;

@Value
public class ExceptionResponse {
    Date date;
    String message;
    String details;
}
