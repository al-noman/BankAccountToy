package com.example.bankaccounttoy.transaction.entity;

import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.stream.Stream;

import static com.example.bankaccounttoy.ApplicationTestUtils.ANOTHER_VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.VALID_IBAN;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntity;
import static com.example.bankaccounttoy.ApplicationTestUtils.createBankAccountEntityWithRandomIban;
import static com.example.bankaccounttoy.ApplicationTestUtils.createTransactionEntity;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private TransactionRepository sut;

    @BeforeEach
    void setUp() {
        var bankAccountEntity1 = createBankAccountEntity();
        var bankAccountEntity2 = createBankAccountEntity(ba -> ba.setIban(ANOTHER_VALID_IBAN));
        var bankAccountEntity3 = createBankAccountEntityWithRandomIban();

        testEntityManager.persist(bankAccountEntity1);
        testEntityManager.persist(bankAccountEntity2);
        testEntityManager.persist(bankAccountEntity3);
        testEntityManager.flush();

        testEntityManager.persist(createTransactionEntity(bankAccountEntity1, bankAccountEntity2, TEN));
        testEntityManager.persist(createTransactionEntity(bankAccountEntity2, bankAccountEntity1, TEN));
        testEntityManager.persist(createTransactionEntity(bankAccountEntity3, bankAccountEntity1, TEN));
        testEntityManager.persist(createTransactionEntity(bankAccountEntity3, bankAccountEntity2, TEN));
        testEntityManager.flush();
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForQueryingTransactionsByBankAccount")
    void findAllByBankAccount(String iban, int expectedNumberOfTransaction) {
        var transactions = sut.findAllByBankAccount(iban);

        assertThat(transactions).hasSize(expectedNumberOfTransaction);
    }

    private static Stream<Arguments> provideArgumentsForQueryingTransactionsByBankAccount() {
        return Stream.of(
                Arguments.of(VALID_IBAN, 3),
                Arguments.of(ANOTHER_VALID_IBAN, 3),
                Arguments.of(RandomStringUtils.randomAlphanumeric(20), 0)
        );
    }
}