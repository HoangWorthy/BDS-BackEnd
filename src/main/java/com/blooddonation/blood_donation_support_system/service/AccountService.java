package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword);

    List<AccountDto> getAllAccounts();

    AccountDto updateUserRole(Long accountId, String newRole);

    AccountDto updateUserStatus(Long accountId, String status);

    AccountDto getAccountById(Long accountId);
}
