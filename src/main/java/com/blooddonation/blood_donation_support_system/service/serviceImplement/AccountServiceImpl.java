package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.mapper.UserDonationHistoryMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.AccountService;
import com.blooddonation.blood_donation_support_system.service.EmailService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserValidator validator;
    public AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword) {
        Account account = validator.getUserOrThrow(accountDto.getId());
        validator.validateUpdatePassword(oldPassword, account.getPassword(), newPassword);
        account.setPassword(passwordEncoder.encode(newPassword));
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto updateUserRole(Long accountId, String newRole) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setRole(Role.valueOf(newRole));
        Account updatedAccount = accountRepository.save(account);
        return AccountMapper.toDto(updatedAccount);
    }


    public AccountDto updateUserStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (status.equals("DISABLE")) {
            account.setStatus(Status.DISABLE);
        } else if (status.equals("ENABLE")) {
            account.setStatus(Status.ENABLE);
        }
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return AccountMapper.toDto(account);
    }

    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }
}
