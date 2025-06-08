package com.blooddonation.blood_donation_support_system.service;

    import com.blooddonation.blood_donation_support_system.dto.AccountDto;
    import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
    import java.util.List;

    public interface AdminService {
        AccountDto updateUserRole(String userEmail, String newRole);

        AccountDto updateUserStatus(String email, String status);

        AccountDto getAccountByEmail(String email);

        ProfileDto getProfileByEmail(String email);

        List<AccountDto> getAllAccounts();

        List<ProfileDto> getAllProfiles();
    }