package com.blooddonation.blood_donation_support_system.service;

    import com.blooddonation.blood_donation_support_system.dto.AccountDto;
    import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
    import java.util.List;

    public interface AdminService {
        AccountDto updateUserRole(Long accountId, String newRole);

        AccountDto updateUserStatus(Long accountId, String status);

        AccountDto getAccountById(Long accountId);

        ProfileDto getProfileById(Long accountId);

        List<AccountDto> getAllAccounts();

        List<ProfileDto> getAllProfiles();
    }