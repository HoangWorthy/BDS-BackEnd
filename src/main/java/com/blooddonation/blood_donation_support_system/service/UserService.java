package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import org.springframework.stereotype.Service;

public interface UserService {
    String registerUser(AccountDto accountDto);

    String resendVerificationCode(String email);

    String verifyUser(String code);

    String login(AccountDto accountDto);

    ProfileDto updateUser(AccountDto accountDto, ProfileDto profileDto);

    AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword);

    String initiatePasswordReset(String email);

    String resetPassword(String resetCode, String newPassword);

    ProfileDto getProfileByEmail(String email);

    void cleanupExpiredRegistrations();
}