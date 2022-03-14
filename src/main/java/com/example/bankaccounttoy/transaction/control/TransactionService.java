package com.example.bankaccounttoy.transaction.control;

import com.example.bankaccounttoy.bankaccount.control.BankAccountService;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountEntity;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountType;
import com.example.bankaccounttoy.transaction.entity.TransferDTO;
import com.example.bankaccounttoy.exceptions.InsufficientBalanceException;
import com.example.bankaccounttoy.exceptions.InvalidBankTransactionException;
import com.example.bankaccounttoy.transaction.entity.TransactionEntity;
import com.example.bankaccounttoy.transaction.entity.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.CHECKING;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.PRIVATE_LOAN;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.SAVINGS;
import static com.example.bankaccounttoy.exceptions.TransactionExceptionType.SAVINGS_ACCOUNT_TO_NON_REFERENCE_ACCOUNT;
import static com.example.bankaccounttoy.exceptions.TransactionExceptionType.WITHDRAWAL_FROM_PRIVATE_LOAN_ACCOUNT;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final BankAccountService bankAccountService;
    private final TransactionRepository transactionRepository;

    public List<TransactionEntity> getAllTransactionsByIban(String iban) {
        return transactionRepository.findAllByBankAccount(iban);
    }

    public TransactionEntity transfer(TransferDTO transferDTO) {
        var transactionEntity = prepareTransactionEntity(transferDTO);
        validateTransaction(transactionEntity);
        updateAccountBalances(transactionEntity);
        return persistTransaction(transactionEntity);
    }

    private TransactionEntity prepareTransactionEntity(TransferDTO transferDTO) {
        var transactionEntity = new TransactionEntity();
        transactionEntity.setSource(getBankAccountByIban(transferDTO.getSourceAccountIban()));
        transactionEntity.setDestination(getBankAccountByIban(transferDTO.getDestinationAccountIban()));
        transactionEntity.setAmount(transferDTO.getAmount());
        return transactionEntity;
    }

    private BankAccountEntity getBankAccountByIban(String iban) {
        return iban == null? null : bankAccountService.find(iban);
    }

    private void validateTransaction(TransactionEntity transactionEntity) {
        if (transactionEntity.getSource() != null) {
            var sourceAccountType = transactionEntity.getSource().getBankAccountType();
            var destinationAccountType = transactionEntity.getDestination().getBankAccountType();

            validateInterBankTransaction(sourceAccountType, destinationAccountType);
            validateTransferability(transactionEntity.getSource(), transactionEntity.getAmount());
        }
    }

    private void validateInterBankTransaction(BankAccountType sourceAccountType, BankAccountType destinationAccountType) {
        if (sourceAccountType.equals(PRIVATE_LOAN)) {
            throw new InvalidBankTransactionException(WITHDRAWAL_FROM_PRIVATE_LOAN_ACCOUNT);
        }
        if (sourceAccountType.equals(SAVINGS) && !destinationAccountType.equals(CHECKING)) {
            throw new InvalidBankTransactionException(SAVINGS_ACCOUNT_TO_NON_REFERENCE_ACCOUNT);
        }
    }

    private void validateTransferability(BankAccountEntity sourceAccount, BigDecimal amount) {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(String.format("Insufficient balance in account: %s", sourceAccount.getIban()));
        }
    }

    private void updateAccountBalances(TransactionEntity transactionEntity) {
        BigDecimal amountToBeTransferred = transactionEntity.getAmount();
        var destination = transactionEntity.getDestination();
        updateBalance(destination, destination.getBalance().add(amountToBeTransferred));

        var source = transactionEntity.getSource();
        if (source != null) {
            updateBalance(source, source.getBalance().subtract(amountToBeTransferred));
        }
    }

    private void updateBalance(BankAccountEntity bankAccountEntity, BigDecimal updatedBalance) {
        bankAccountEntity.setBalance(updatedBalance);
        bankAccountService.persist(bankAccountEntity);
    }

    private TransactionEntity persistTransaction(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }
}
