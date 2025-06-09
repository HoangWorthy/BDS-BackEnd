package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;

import java.util.List;

public interface UserService {
    String registerUser(AccountDto accountDto);

    String resendVerificationCode(String email);

    String verifyUser(String code);

    String login(AccountDto accountDto);

    ProfileDto updateUser(AccountDto accountDto, ProfileDto profileDto);

    AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword);

    String initiatePasswordReset(String email);

    String resetPassword(String resetCode, String newPassword);

    ProfileDto getProfileById(Long accountId);

    List<UserDonationHistoryDto> getDonationHistory(Long accountId);

    void cleanupExpiredRegistrations();
}