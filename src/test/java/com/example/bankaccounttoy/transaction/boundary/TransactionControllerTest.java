package com.example.bankaccounttoy.transaction.boundary;

import com.example.bankaccounttoy.transaction.control.TransactionService;
import com.example.bankaccounttoy.transaction.entity.TransactionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.bankaccounttoy.ApplicationTestUtils.VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.createTransactionDTO;
import static com.example.bankaccounttoy.ApplicationTestUtils.createTransferDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    @InjectMocks
    private TransactionController sut;
    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionMapper transactionMapper;

    @Test
    void transfer_shouldDelegateToRepositoryAndRespondWithHttpStatusOk() {
        var transferDTO = createTransferDTO(null, null);
        var transactionDTO = createTransactionDTO();
        when(transactionMapper.toDto(any())).thenReturn(transactionDTO);

        var response = sut.transfer(transferDTO);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(transactionDTO);
        verify(transactionService).transfer(transferDTO);
    }

    @Test
    void getAllTransactions_shouldDelegateToRepositoryAndRespondWithHttpStatusOk() {
        var transactionList = List.of(createTransactionDTO(), createTransactionDTO());
        when(transactionMapper.toDtoList(anyList())).thenReturn(transactionList);

        var response = sut.getAllTransactions(VALID_IBAN);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).containsExactlyInAnyOrderElementsOf(transactionList);
        verify(transactionService).getAllTransactionsByIban(VALID_IBAN);
    }
}