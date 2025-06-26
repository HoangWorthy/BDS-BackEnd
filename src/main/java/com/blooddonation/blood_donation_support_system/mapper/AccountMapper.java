package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.enums.AccountStatus;
import com.blooddonation.blood_donation_support_system.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class    AccountMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

//    public static AccountDto toDto(Account account) {
//        if (account == null) return null;
//
//        return AccountDto.builder()
//                .id(account.getId())
//                .email(account.getEmail())
//                .password(account.getPassword())
//                .role(account.getRole())
//                .status(account.getStatus())
//                .avatar(account.getAvatar())
//                .build();
//    }

//    public static AccountDto toDto(Long id, String email, String password, Role role, AccountStatus status, String avatar) {
//        return AccountDto.builder()
//                .id(id)
//                .email(email)
//                .password(password)
//                .role(role)
//                .status(status)
//                .avatar(avatar)
//                .build();
//    }

    public static Account toEntity(AccountDto accountDto) {
        if (accountDto == null) return null;

        return Account.builder()
                .id(accountDto.getId())
                .email(accountDto.getEmail())
                .password(accountDto.getPassword())
                .role(Role.MEMBER)
                .status(AccountStatus.ENABLE)
                .avatar(accountDto.getAvatar())
                .build();
    }
}

