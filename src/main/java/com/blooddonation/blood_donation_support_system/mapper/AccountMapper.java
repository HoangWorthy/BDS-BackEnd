package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public static AccountDto toDto(Account account) {
        if (account == null) return null;

        return AccountDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .password(account.getPassword())
                .role(account.getRole())
                .status(account.getStatus())
                .build();
    }

    public static Account toEntity(AccountDto accountDto) {
        if (accountDto == null) return null;

        return Account.builder()
                .id(accountDto.getId())
                .email(accountDto.getEmail())
                .password(accountDto.getPassword())
                .role(accountDto.getRole())
                .status(accountDto.getStatus())
                .build();
    }
}
