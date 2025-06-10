package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.mapper.UserDonationHistoryMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.EmailService;
import com.blooddonation.blood_donation_support_system.service.ProfileService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    @Autowired
    private UserDonationHistoryMapper userDonationHistoryMapper;
    @Autowired
    private UserValidator validator;

    @Transactional
    public ProfileDto updateUser(AccountDto accountDto, ProfileDto profileDto) {

        Account account = validator.getUserOrThrow(accountDto.getId());
        Profile profile = validator.getProfileOrThrow(account.getProfile());

        ProfileMapper.updateEntityFromDto(profile, profileDto);

        Profile updatedProfile = profileRepository.save(profile);
        return ProfileMapper.toDto(updatedProfile);
    }

    public ProfileDto getProfileById(Long accountId) {
        Account account = validator.getUserOrThrow(accountId);
        Profile profile = validator.getProfileOrThrow(account.getProfile());
        return ProfileMapper.toDto(profile);
    }

    @Transactional
    public List<UserDonationHistoryDto> getDonationHistory(Long accountId) {
        Account account = validator.getUserOrThrow(accountId);
        List<EventRegistration> registrations = eventRegistrationRepository.findByAccount(account);
        return userDonationHistoryMapper.toDtoList(registrations);
    }

    public List<ProfileDto> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream().map(ProfileMapper::toDto)
                .collect(Collectors.toList());
    }
}
