package com.example.bankaccounttoy.bankaccount.entity;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountEntity toEntity(BankAccountDTO bankAccountDTO);

    BankAccountDTO toDto(BankAccountEntity bankAccountEntity);

    List<BankAccountDTO> toDtoList(List<BankAccountEntity> bankAccountEntities);
}
