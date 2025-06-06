package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public static ProfileDto toDto(Profile Profile) {
        if (Profile == null) return null;

        return ProfileDto.builder()
                .id(Profile.getId())
                .name(Profile.getName())
                .phone(Profile.getPhone())
                .address(Profile.getAddress())
                .bloodType(Profile.getBloodType())
                .gender(Profile.getGender())
                .dateOfBirth(Profile.getDateOfBirth())
                .lastDonationDate(Profile.getLastDonationDate())
                .nextEligibleDonationDate(Profile.getNextEligibleDonationDate())
                .personalId(Profile.getPersonalId())
                .build();
    }

    public static Profile toEntity(ProfileDto profileDto) {
        if (profileDto == null) return null;

        return Profile.builder()
                .id(profileDto.getId())
                .name(profileDto.getName())
                .phone(profileDto.getPhone())
                .address(profileDto.getAddress())
                .bloodType(profileDto.getBloodType())
                .gender(profileDto.getGender())
                .dateOfBirth(profileDto.getDateOfBirth())
                .lastDonationDate(profileDto.getLastDonationDate())
                .nextEligibleDonationDate(profileDto.getNextEligibleDonationDate())
                .personalId(profileDto.getPersonalId())
                .build();
    }
}
