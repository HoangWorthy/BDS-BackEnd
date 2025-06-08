package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public static ProfileDto toDto(Profile Profile) {
        if (Profile == null) return null;

        return ProfileDto.builder()
                .id(Profile.getId())
                .accountId(Profile.getAccountId())
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
                .accountId(profileDto.getAccountId())
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

    // New method to update existing entity from DTO
    public static void updateEntityFromDto(Profile profile, ProfileDto dto) {
        if (profile == null || dto == null) return;

        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setBloodType(dto.getBloodType());
        profile.setGender(dto.getGender());
        profile.setDateOfBirth(dto.getDateOfBirth());
        profile.setPersonalId(dto.getPersonalId());

        if (profile.getLastDonationDate() == null) {
            profile.setLastDonationDate(dto.getLastDonationDate());
        }
        if (profile.getNextEligibleDonationDate() == null) {
            profile.setNextEligibleDonationDate(dto.getLastDonationDate());
        }
    }

}
