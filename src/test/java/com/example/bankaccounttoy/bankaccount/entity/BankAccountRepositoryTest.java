package com.example.bankaccounttoy.bankaccount.entity;

import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.stream.Stream;

import static com.example.bankaccounttoy.ApplicationTestUtils.VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntity;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.CHECKING;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.PRIVATE_LOAN;
import static com.example.bankaccounttoy.bankaccount.entity.BankAccountType.SAVINGS;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankAccountRepositoryTest {
    private static final BankAccountEntity REFERENCE_BANK_ACCOUNT_ENTITY = createBankAccountEntity();

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BankAccountRepository sut;

    @BeforeEach
    void setUp() {
        entityManager.persist(REFERENCE_BANK_ACCOUNT_ENTITY);
        entityManager.persist(createBankAccountWithRandomIbanAndProvidedType(CHECKING));
        entityManager.persist(createBankAccountWithRandomIbanAndProvidedType(SAVINGS));
        entityManager.persist(createBankAccountWithRandomIbanAndProvidedType(SAVINGS));
        entityManager.persist(createBankAccountWithRandomIbanAndProvidedType(PRIVATE_LOAN));
        entityManager.flush();
    }

    @Test
    void findByIban_shouldReturnReferenceBankAccountEntity() {
        var bankAccountEntity = sut.findByIban(VALID_IBAN);

        assertThat(bankAccountEntity).isPresent().contains(REFERENCE_BANK_ACCOUNT_ENTITY);
    }

    @ParameterizedTest(name = "[{index}] -> Bank account of type {0} has {1} entities")
    @MethodSource("provideParamsForFindAllByTypes")
    void findByTypes(List<BankAccountType> types, int expectedNumberOfAccounts) {
        var bankAccounts = sut.findByTypes(types);

        assertThat(bankAccounts).hasSize(expectedNumberOfAccounts);
    }

    private static Stream<Arguments> provideParamsForFindAllByTypes() {
        return Stream.of(
                Arguments.of(List.of(CHECKING), 2),
                Arguments.of(List.of(CHECKING, SAVINGS), 4),
                Arguments.of(List.of(CHECKING, PRIVATE_LOAN), 3),
                Arguments.of(List.of(SAVINGS, PRIVATE_LOAN), 3),
                Arguments.of(List.of(PRIVATE_LOAN), 1),
                Arguments.of(List.of(CHECKING, SAVINGS, PRIVATE_LOAN), 5),
                Arguments.of(List.of(), 0)
        );
    }

    private BankAccountEntity createBankAccountWithRandomIbanAndProvidedType(BankAccountType type) {
        var bankAccountEntity = createBankAccountEntity(entity -> entity.setBankAccountType(type));
        bankAccountEntity.setIban(RandomStringUtils.randomAlphanumeric(22));
        return bankAccountEntity;
    }
}