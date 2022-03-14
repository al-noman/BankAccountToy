package com.example.bankaccounttoy.bankaccount.boundary;

import com.example.bankaccounttoy.bankaccount.control.BankAccountService;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountDTO;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountMapper;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bank-accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final BankAccountMapper bankAccountMapper;

    @PostMapping
    public ResponseEntity<BankAccountDTO> create(@RequestBody @Validated BankAccountDTO bankAccountDTO) {
        var bankAccountEntity = bankAccountService.persist(bankAccountMapper.toEntity(bankAccountDTO));
        return ResponseEntity.status(CREATED)
                .body(bankAccountMapper.toDto(bankAccountEntity));
    }

    @GetMapping("/{IBAN}/current-balance")
    public ResponseEntity<BigDecimal> showCurrentBalance(@PathVariable("IBAN") String iban) {
        return ResponseEntity.ok(bankAccountService.showCurrentBalance(iban));
    }

    @GetMapping("/filter-by-type")
    public ResponseEntity<List<BankAccountDTO>> filterByType(
            @RequestParam(name = "types", required = false) List<BankAccountType> accountTypes
    ) {
        var bankAccountEntities = bankAccountService.findByTypes(accountTypes);
        return ResponseEntity.ok(bankAccountMapper.toDtoList(bankAccountEntities));
    }

    @PutMapping("/{IBAN}/lock")
    public ResponseEntity<BankAccountDTO> lock(@PathVariable("IBAN") String iban) {
        var bankAccountEntity = bankAccountService.lock(iban);
        return ResponseEntity.ok(bankAccountMapper.toDto(bankAccountEntity));
    }

    @PutMapping("/{IBAN}/unlock")
    public ResponseEntity<BankAccountDTO> unlock(@PathVariable("IBAN") String iban) {
        var bankAccountEntity = bankAccountService.unlock(iban);
        return ResponseEntity.ok(bankAccountMapper.toDto(bankAccountEntity));
    }
}
