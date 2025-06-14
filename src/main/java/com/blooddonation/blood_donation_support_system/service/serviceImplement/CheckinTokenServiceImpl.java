package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.CheckinTokenDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileWithFormResponseDto;
import com.blooddonation.blood_donation_support_system.entity.CheckinToken;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.mapper.CheckinTokenMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.repository.CheckinTokenRepository;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CheckinTokenServiceImpl implements CheckinTokenService {

    @Autowired
    private CheckinTokenRepository checkinTokenRepository;
    @Autowired
    private DonationEventValidator validator;

    @Override
    public CheckinTokenDto generateTokenForProfile(Profile profile, DonationEvent donationEvent) {
        CheckinToken token = new CheckinToken();
        token.setToken(UUID.randomUUID().toString());
        token.setProfile(profile);
        token.setCreationDate(LocalDate.now());
        token.setExpirationDate(donationEvent.getDonationDate().plusDays(1));

        checkinTokenRepository.save(token);
        return CheckinTokenMapper.toDto(token);
    }

    @Override
    @Transactional
    public ProfileWithFormResponseDto getProfileFromToken(String token, String email, Long eventId) {
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        CheckinToken checkinToken = checkinTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (checkinToken.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Token has expired");
        }

        Profile profile = checkinToken.getProfile();
        EventRegistration eventRegistration = validator.getRegistrationOrThrow(profile.getPersonalId(), donationEvent);
        String jsonForm = eventRegistration.getJsonForm();
        return new ProfileWithFormResponseDto(ProfileMapper.toDto(profile), jsonForm) ; // Convert to DTO
    }


}
