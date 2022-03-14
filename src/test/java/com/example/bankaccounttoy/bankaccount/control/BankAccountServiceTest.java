package com.example.bankaccounttoy.bankaccount.control;

import com.example.bankaccounttoy.bankaccount.entity.BankAccountRepository;
import com.example.bankaccounttoy.exceptions.InvalidIbanException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.example.bankaccounttoy.ApplicationTestUtils.INITIAL_BALANCE;
import static com.example.bankaccounttoy.ApplicationTestUtils.NON_REFERENCED_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntity;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.CHECKING;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.SAVINGS;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
    @InjectMocks
    private BankAccountService sut; // SUT: Service Under Test [N.B: Not specific to services only ;)]
    @Mock
    private BankAccountRepository repository;

    @Test
    void persist_shouldDeleteToRepositoryByInvokingSaveMethodOnIt() {
        var bankAccountEntity = createBankAccountEntity();

        sut.persist(bankAccountEntity);

        verify(repository).save(bankAccountEntity);
    }

    @Test
    void find_shouldDelegateToRepositoryAndReturnCorrectAccountWhenIbanIsNotBlank() {
        var bankAccountEntity = createBankAccountEntity();
        when(repository.findByIban(VALID_IBAN)).thenReturn(Optional.of(bankAccountEntity));

        var result = sut.find(VALID_IBAN);

        assertThat(result).isEqualTo(bankAccountEntity);
        verify(repository).findByIban(VALID_IBAN);
    }

    @Test
    void find_shouldThrowInvalidIbanExceptionWhenIbanIsBlank() {
        assertThatThrownBy(() -> sut.find(""))
                .isInstanceOf(InvalidIbanException.class)
                .hasMessageContaining("Invalid IBAN provided");
    }

    @Test
    void find_shouldThrowEntityNotFoundExceptionWhenNoBankAccountFoundReferencedByTheIban() {
        when(repository.findByIban(NON_REFERENCED_IBAN)).thenReturn(empty());

        assertThatThrownBy(() -> sut.find(NON_REFERENCED_IBAN))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No bank account referenced by IBAN: [%s] is found", NON_REFERENCED_IBAN);
    }

    @Test
    void showCurrentBalance_shouldReturnCorrectResult() {
        var bankAccountEntity = createBankAccountEntity();
        when(repository.findByIban(VALID_IBAN)).thenReturn(Optional.of(bankAccountEntity));

        var result = sut.showCurrentBalance(VALID_IBAN);

        assertThat(result).isEqualByComparingTo(INITIAL_BALANCE);
    }

    @Test
    void findByTypes_shouldDelegateToRepository() {
        var bankAccountTypes = List.of(CHECKING, SAVINGS);
        sut.findByTypes(bankAccountTypes);

        verify(repository).findByTypes(bankAccountTypes);
    }

    @Test
    void lock_shouldUpdateTheEntityPropertyAndPersistItByDelegatingToRepository() {
        var bankAccountEntity = createBankAccountEntity();
        when(repository.findByIban(VALID_IBAN)).thenReturn(Optional.of(bankAccountEntity));
        when(repository.save(any())).thenReturn(any());

        sut.lock(VALID_IBAN);

        assertThat(bankAccountEntity.isLocked()).isTrue();
        verify(repository).save(bankAccountEntity);
    }

    @Test
    void unlock_shouldUpdateTheEntityPropertyAndPersistItByDelegatingToRepository() {
        var bankAccountEntity = createBankAccountEntity(entity -> entity.setLocked(true));
        when(repository.findByIban(VALID_IBAN)).thenReturn(Optional.of(bankAccountEntity));
        when(repository.save(any())).thenReturn(any());

        sut.unlock(VALID_IBAN);

        assertThat(bankAccountEntity.isLocked()).isFalse();
        verify(repository).save(bankAccountEntity);
    }
}