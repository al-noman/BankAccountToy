package com.example.bankaccounttoy.transaction.boundary;

import com.example.bankaccounttoy.transaction.entity.TransferDTO;
import com.example.bankaccounttoy.transaction.control.TransactionService;
import com.example.bankaccounttoy.transaction.entity.TransactionDTO;
import com.example.bankaccounttoy.transaction.entity.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@RequestBody @Validated TransferDTO transferDTO) {
        var resultedTransaction = transactionService.transfer(transferDTO);
        return ResponseEntity.ok(transactionMapper.toDto(resultedTransaction));
    }

    @GetMapping("/by-account/{IBAN}")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(@PathVariable("IBAN") String iban) {
        var transactionEntities = transactionService.getAllTransactionsByIban(iban);
        return ResponseEntity.ok(transactionMapper.toDtoList(transactionEntities));
    }
}
