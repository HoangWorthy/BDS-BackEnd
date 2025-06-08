package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;

    public AccountDto updateUserRole(String userEmail, String newRole){
        Account account = accountRepository.findByEmail(userEmail);
        if (account == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        } else {
            account.setRole(Role.valueOf(newRole));
            Account updatedAccount = accountRepository.save(account);
            return AccountMapper.toDto(updatedAccount);
        }
    }

    public AccountDto updateUserStatus(String email, String status) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Account not found");
        }

        if (status.equals("DISABLE")) {
            account.setStatus(Status.DISABLE);
        } else if (status.equals("ENABLE")) {
            account.setStatus(Status.ENABLE);
        }
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }

    public AccountDto getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return AccountMapper.toDto(account);
    }

    public ProfileDto getProfileByEmail(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        Profile profile = account.getProfile();
        if (profile == null) {
            throw new RuntimeException("Profile not found for user: " + email);
        }
        return ProfileMapper.toDto(profile);
    }

    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProfileDto> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream().map(ProfileMapper::toDto)
                .collect(Collectors.toList());
    }
}
