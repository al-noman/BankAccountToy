package com.example.bankaccounttoy.transaction.entity;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDTO toDto(TransactionEntity transactionEntity);

    List<TransactionDTO> toDtoList(List<TransactionEntity> transactionEntities);
}
