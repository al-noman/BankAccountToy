package com.example.bankaccounttoy.bankaccount.boundary;

import com.example.bankaccounttoy.bankaccount.control.BankAccountService;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static com.example.bankaccounttoy.ApplicationTestUtils.INITIAL_BALANCE;
import static com.example.bankaccounttoy.ApplicationTestUtils.VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountDTO;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntity;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.CHECKING;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.SAVINGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {
    @InjectMocks
    private BankAccountController sut; // SUT: Service Under Test [N.B: Not specific to services only ;)]
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private BankAccountMapper bankAccountMapper;

    @Test
    void create_shouldDelegateToServiceAndRespondWithHttpStatusCreated() {
        var bankAccountDTO = createBankAccountDTO();
        var bankAccountEntity = createBankAccountEntity();

        when(bankAccountMapper.toEntity(bankAccountDTO)).thenReturn(bankAccountEntity);
        when(bankAccountService.persist(bankAccountEntity)).thenReturn(bankAccountEntity);
        when(bankAccountMapper.toDto(bankAccountEntity)).thenReturn(bankAccountDTO);

        var response = sut.create(bankAccountDTO);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo(bankAccountDTO);
        verify(bankAccountService).persist(bankAccountEntity);
    }

    @Test
    void showCurrentBalance_shouldDelegateToServiceAndRespondWithHttpStatusOk() {
        when(bankAccountService.showCurrentBalance(VALID_IBAN)).thenReturn(INITIAL_BALANCE);

        var response = sut.showCurrentBalance(VALID_IBAN);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(Objects.requireNonNull(response.getBody()).compareTo(INITIAL_BALANCE)).isZero();
        verify(bankAccountService).showCurrentBalance(VALID_IBAN);
    }

    @Test
    void filterByType_shouldDelegateToServiceAndRespondWithHttpStatusOk() {
        var accountTypeList = List.of(CHECKING, SAVINGS);
        var bankAccountEntities = List.of(
                createBankAccountEntity(),
                createBankAccountEntity((entity) -> entity.setBankAccountType(SAVINGS))
        );
        var bankAccountDTOS = List.of(
                createBankAccountDTO(),
                createBankAccountDTO((dto) -> dto.setBankAccountType(SAVINGS))
        );
        when(bankAccountService.findByTypes(accountTypeList)).thenReturn(bankAccountEntities);
        when(bankAccountMapper.toDtoList(bankAccountEntities)).thenReturn(bankAccountDTOS);

        var response = sut.filterByType(accountTypeList);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).containsExactlyInAnyOrderElementsOf(bankAccountDTOS);
        verify(bankAccountService).findByTypes(accountTypeList);
    }

    @Test
    void lock_shouldDelegateToServiceAndRespondWithHttpStatusOk() {
        var bankAccountEntity = createBankAccountEntity();
        var bankAccountDTO = createBankAccountDTO();

        when(bankAccountService.lock(VALID_IBAN)).thenReturn(bankAccountEntity);
        when(bankAccountMapper.toDto(bankAccountEntity)).thenReturn(bankAccountDTO);

        var response = sut.lock(VALID_IBAN);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(bankAccountDTO);
        verify(bankAccountService).lock(VALID_IBAN);
    }

    @Test
    void unlock_shouldDelegateToServiceAndRespondWithHttpStatusOk() {
        var bankAccountEntity = createBankAccountEntity();
        var bankAccountDTO = createBankAccountDTO();

        when(bankAccountService.unlock(VALID_IBAN)).thenReturn(bankAccountEntity);
        when(bankAccountMapper.toDto(bankAccountEntity)).thenReturn(bankAccountDTO);

        var response = sut.unlock(VALID_IBAN);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(bankAccountDTO);
        verify(bankAccountService).unlock(VALID_IBAN);
    }
}