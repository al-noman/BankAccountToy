package com.example.bankaccounttoy.transaction.control;

import com.example.bankaccounttoy.bankaccount.control.BankAccountService;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountType;
import com.example.bankaccounttoy.exceptions.InsufficientBalanceException;
import com.example.bankaccounttoy.exceptions.InvalidBankTransactionException;
import com.example.bankaccounttoy.transaction.entity.TransactionRepository;
import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.example.bankaccounttoy.ApplicationTestUtils.ANOTHER_VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.DESTINATION_ACCOUNT_BALANCE_AFTER_TRANSFER;
import static com.example.bankaccounttoy.ApplicationTestUtils.SOURCE_ACCOUNT_BALANCE_AFTER_TRANSFER;
import static com.example.bankaccounttoy.ApplicationTestUtils.VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntity;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntityWithIbanAndType;
import static com.example.bankaccounttoy.ApplicationTestUtils.createTransactionEntity;
import static com.example.bankaccounttoy.ApplicationTestUtils.createTransferDTO;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.CHECKING;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.PRIVATE_LOAN;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.SAVINGS;
import static com.example.bankaccounttoy.exceptions.TransactionExceptionType.SAVINGS_ACCOUNT_TO_NON_REFERENCE_ACCOUNT;
import static com.example.bankaccounttoy.exceptions.TransactionExceptionType.WITHDRAWAL_FROM_PRIVATE_LOAN_ACCOUNT;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService sut;
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private TransactionRepository repository;

    @Test
    void getAllTransactionsByIban_shouldDelegateToRepository() {
        sut.getAllTransactionsByIban(VALID_IBAN);

        verify(repository).findAllByBankAccount(VALID_IBAN);
    }

    @ParameterizedTest(name = "[{index}] -> Depositing money to {1} account")
    @MethodSource("provideParamsForDepositingToAllBankAccountType")
    void transfer_shouldAllowDepositingToAllTypesOfBankAccounts(
            String iban,
            BankAccountType type
    ) {
        var transferDTO = createTransferDTO(null, iban);
        var retrievedBankAccount = createBankAccountEntityWithIbanAndType(iban, type);
        var transactionEntity = createTransactionEntity(null, retrievedBankAccount, transferDTO.getAmount());

        when(bankAccountService.find(iban)).thenReturn(retrievedBankAccount);
        when(repository.save(any())).thenReturn(transactionEntity);

        var result = sut.transfer(transferDTO);

        assertThat(result).extracting("destination.iban", "destination.bankAccountType").containsExactly(iban, type);
        assertThat(retrievedBankAccount.getBalance()).isEqualByComparingTo(DESTINATION_ACCOUNT_BALANCE_AFTER_TRANSFER);
        verify(bankAccountService).persist(retrievedBankAccount);
        verify(repository).save(transactionEntity);
    }

    private static Stream<Arguments> provideParamsForDepositingToAllBankAccountType() {
        return Stream.of(
                Arguments.of(VALID_IBAN, CHECKING),
                Arguments.of(ANOTHER_VALID_IBAN, SAVINGS),
                Arguments.of(RandomStringUtils.randomAlphanumeric(22), PRIVATE_LOAN)
        );
    }

    @Test
    void transfer_shouldAllowInterBankTransaction() {
        var transferDTO = createTransferDTO(VALID_IBAN, ANOTHER_VALID_IBAN);
        var sourceAccount = createBankAccountEntity(ba -> ba.setBankAccountType(SAVINGS));
        var destinationAccount = createBankAccountEntity(ba -> ba.setBankAccountType(CHECKING));
        var transactionEntity = createTransactionEntity(sourceAccount, destinationAccount, transferDTO.getAmount());

        when(bankAccountService.find(VALID_IBAN)).thenReturn(sourceAccount);
        when(bankAccountService.find(ANOTHER_VALID_IBAN)).thenReturn(destinationAccount);
        when(repository.save(any())).thenReturn(transactionEntity);

        sut.transfer(transferDTO);

        assertThat(sourceAccount.getBalance()).isEqualByComparingTo(SOURCE_ACCOUNT_BALANCE_AFTER_TRANSFER);
        assertThat(destinationAccount.getBalance()).isEqualByComparingTo(DESTINATION_ACCOUNT_BALANCE_AFTER_TRANSFER);
        verify(bankAccountService).persist(sourceAccount);
        verify(bankAccountService).persist(destinationAccount);
        verify(repository).save(transactionEntity);
    }

    @Test
    void transfer_shouldThrowInsufficientBalanceExceptionWhenNotEnoughBalance() {
        var transferDTO = createTransferDTO(VALID_IBAN, ANOTHER_VALID_IBAN);
        var sourceAccount = createBankAccountEntity(ba -> ba.setBalance(ZERO));
        var destinationAccount = createBankAccountEntity(ba -> ba.setBankAccountType(CHECKING));

        when(bankAccountService.find(VALID_IBAN)).thenReturn(sourceAccount);
        when(bankAccountService.find(ANOTHER_VALID_IBAN)).thenReturn(destinationAccount);

        assertThatThrownBy(() -> sut.transfer(transferDTO))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance in account: %s", sourceAccount.getIban());
    }

    @Test
    void transfer_shouldThrowInvalidBankTransactionExceptionWhenWithdrawingFromPrivateLoanAccount() {
        var transferDTO = createTransferDTO(VALID_IBAN, ANOTHER_VALID_IBAN);
        var sourceAccount = createBankAccountEntity(ba -> ba.setBankAccountType(PRIVATE_LOAN));
        var destinationAccount = createBankAccountEntity(ba -> ba.setBankAccountType(CHECKING));

        when(bankAccountService.find(VALID_IBAN)).thenReturn(sourceAccount);
        when(bankAccountService.find(ANOTHER_VALID_IBAN)).thenReturn(destinationAccount);

        assertThatThrownBy(() -> sut.transfer(transferDTO))
                .isInstanceOf(InvalidBankTransactionException.class)
                .hasMessageContaining(String.valueOf(WITHDRAWAL_FROM_PRIVATE_LOAN_ACCOUNT));
    }

    @Test
    void transfer_shouldThrowInvalidBankTransactionExceptionWhenSourceAccountIsOfTypeSavingsAndDestinationIsNotChecking() {
        var transferDTO = createTransferDTO(VALID_IBAN, ANOTHER_VALID_IBAN);
        var sourceAccount = createBankAccountEntity(ba -> ba.setBankAccountType(SAVINGS));
        var destinationAccount = createBankAccountEntity(ba -> ba.setBankAccountType(SAVINGS));

        when(bankAccountService.find(VALID_IBAN)).thenReturn(sourceAccount);
        when(bankAccountService.find(ANOTHER_VALID_IBAN)).thenReturn(destinationAccount);

        assertThatThrownBy(() -> sut.transfer(transferDTO))
                .isInstanceOf(InvalidBankTransactionException.class)
                .hasMessageContaining(String.valueOf(SAVINGS_ACCOUNT_TO_NON_REFERENCE_ACCOUNT));
    }
}