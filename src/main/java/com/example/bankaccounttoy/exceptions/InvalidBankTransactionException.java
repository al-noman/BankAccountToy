package com.example.bankaccounttoy.exceptions;

public class InvalidBankTransactionException extends RuntimeException {
    public InvalidBankTransactionException(TransactionExceptionType transactionExceptionType) {
        super(String.valueOf(transactionExceptionType));
    }
}
