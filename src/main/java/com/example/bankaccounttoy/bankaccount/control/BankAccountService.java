package com.example.bankaccounttoy.bankaccount.control;

import com.example.bankaccounttoy.bankaccount.entity.BankAccountEntity;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountRepository;
import com.example.bankaccounttoy.bankaccount.entity.BankAccountType;
import com.example.bankaccounttoy.exceptions.InvalidIbanException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountEntity persist(BankAccountEntity bankAccountEntity) {
        return bankAccountRepository.save(bankAccountEntity);
    }

    public BankAccountEntity find(String iban) {
        if (iban.isBlank()) {
            throw new InvalidIbanException("Invalid IBAN provided");
        }
        return bankAccountRepository.findByIban(iban).orElseThrow(
                () -> new EntityNotFoundException(String.format("No bank account referenced by IBAN: [%s] is found", iban))
        );
    }

    public BigDecimal showCurrentBalance(String iban) {
        return find(iban).getBalance();
    }

    public List<BankAccountEntity> findByTypes(List<BankAccountType> types) {
        return bankAccountRepository.findByTypes(types);
    }

    public BankAccountEntity lock(String iban) {
        var bankAccountEntity = find(iban);
        bankAccountEntity.setLocked(true);
        return bankAccountRepository.save(bankAccountEntity);
    }

    public BankAccountEntity unlock(String iban) {
        var bankAccountEntity = find(iban);
        bankAccountEntity.setLocked(false);
        return bankAccountRepository.save(bankAccountEntity);
    }
}
