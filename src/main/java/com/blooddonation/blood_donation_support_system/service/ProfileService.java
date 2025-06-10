package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;

import java.util.List;

public interface ProfileService {
    ProfileDto updateUser(AccountDto accountDto, ProfileDto profileDto);

    ProfileDto getProfileById(Long accountId);

    List<UserDonationHistoryDto> getDonationHistory(Long accountId);

    List<ProfileDto> getAllProfiles();
}
