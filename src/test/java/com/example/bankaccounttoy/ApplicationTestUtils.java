package com.example.bankaccounttoy;

import com.example.bankaccounttoy.bankaccount.entity.BankAccountDTO;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountEntity;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountType;
import com.example.bankaccounttoy.transaction.entity.TransferDTO;
import com.example.bankaccounttoy.transaction.entity.TransactionDTO;
import com.example.bankaccounttoy.transaction.entity.TransactionEntity;
import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.CHECKING;

public class ApplicationTestUtils {
    public static final String VALID_IBAN = "DE75512108001245126199";
    public static final String ANOTHER_VALID_IBAN = "AL35202111090000000001234567";
    public static final String NON_REFERENCED_IBAN = "DE75512108001245126166";

    public static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(3500);
    public static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(2500);
    public static final BigDecimal DESTINATION_ACCOUNT_BALANCE_AFTER_TRANSFER = BigDecimal.valueOf(6000);
    public static final BigDecimal SOURCE_ACCOUNT_BALANCE_AFTER_TRANSFER = BigDecimal.valueOf(1000);

    public static final UUID TRANSACTION_ID = UUID.randomUUID();

    public static BankAccountDTO createBankAccountDTO(Consumer<BankAccountDTO> customSetter) {
        var bankAccountDTO = createBankAccountDTO();
        customSetter.accept(bankAccountDTO);
        return bankAccountDTO;
    }

    public static BankAccountDTO createBankAccountDTO() {
        var bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setIban(VALID_IBAN);
        bankAccountDTO.setBankAccountType(CHECKING);
        bankAccountDTO.setBalance(INITIAL_BALANCE);
        bankAccountDTO.setLocked(false);
        return bankAccountDTO;
    }

    public static BankAccountEntity createBankAccountEntity(Consumer<BankAccountEntity> customSetter) {
        var bankAccountEntity = createBankAccountEntity();
        customSetter.accept(bankAccountEntity);
        return bankAccountEntity;
    }

    public static BankAccountEntity createBankAccountEntity() {
        var bankAccountEntity = new BankAccountEntity();
        bankAccountEntity.setIban(VALID_IBAN);
        bankAccountEntity.setBankAccountType(CHECKING);
        bankAccountEntity.setBalance(INITIAL_BALANCE);
        bankAccountEntity.setLocked(false);
        return bankAccountEntity;
    }

    public static BankAccountEntity createBankAccountEntityWithIbanAndType(String iban, BankAccountType type) {
        var bankAccountEntity = createBankAccountEntity(ba -> ba.setBankAccountType(type));
        bankAccountEntity.setIban(iban);
        return bankAccountEntity;
    }

    public static BankAccountEntity createBankAccountEntityWithRandomIban() {
        return createBankAccountEntity(ba -> ba.setIban(RandomStringUtils.randomAlphanumeric(22)));
    }

    public static TransferDTO createTransferDTO(String sourceAccountIban, String destinationAccountIban) {
        destinationAccountIban = (destinationAccountIban == null) ? VALID_IBAN : destinationAccountIban;
        return new TransferDTO(sourceAccountIban, destinationAccountIban, TRANSFER_AMOUNT);
    }

    public static TransactionDTO createTransactionDTO(Consumer<TransactionDTO> customSetter) {
        var transactionDto = createTransactionDTO();
        customSetter.accept(transactionDto);
        return transactionDto;
    }

    public static TransactionDTO createTransactionDTO() {
        var transactionDTO = new TransactionDTO();
        transactionDTO.setId(TRANSACTION_ID);
        transactionDTO.setSource(createBankAccountDTO(ba -> ba.setIban(RandomStringUtils.randomAlphanumeric(22))));
        transactionDTO.setDestination(createBankAccountDTO());
        transactionDTO.setAmount(TRANSFER_AMOUNT);
        return transactionDTO;
    }

    public static TransactionEntity createTransactionEntity(Consumer<TransactionEntity> customSetter) {
        var transactionEntity = createTransactionEntity();
        customSetter.accept(transactionEntity);
        return transactionEntity;
    }

    public static TransactionEntity createTransactionEntity() {
        var transactionEntity = new TransactionEntity();
        transactionEntity.setId(TRANSACTION_ID);
        transactionEntity.setSource(createBankAccountEntity(ba -> ba.setIban(RandomStringUtils.randomAlphanumeric(22))));
        transactionEntity.setDestination(createBankAccountEntity());
        transactionEntity.setAmount(TRANSFER_AMOUNT);
        return transactionEntity;
    }

    public static TransactionEntity createTransactionEntity(BankAccountEntity sourceAccount, BankAccountEntity destinationAccount, BigDecimal transferAmount) {
        var transactionEntity = new TransactionEntity();
        transactionEntity.setSource(sourceAccount);
        transactionEntity.setDestination(destinationAccount);
        transactionEntity.setAmount(transferAmount);
        return transactionEntity;
    }
}
