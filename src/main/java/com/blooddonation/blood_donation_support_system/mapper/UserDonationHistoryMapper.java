package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.UserDonationHistory;
import org.springframework.stereotype.Component;

@Component
public class UserDonationHistoryMapper {
    public static UserDonationHistoryDto toDto(UserDonationHistory userDonationHistory) {
        if (userDonationHistory == null) {
            return null;
        }

        UserDonationHistoryDto dto = new UserDonationHistoryDto();
        dto.setId(userDonationHistory.getId());
        dto.setAccountId(userDonationHistory.getAccount() != null ? userDonationHistory.getAccount().getId() : null);
        dto.setEventId(userDonationHistory.getEvent() != null ? userDonationHistory.getEvent().getId() : null);
        dto.setRegistrationId(userDonationHistory.getRegistration() != null ? userDonationHistory.getRegistration().getId() : null);

        return dto;
    }

    public static UserDonationHistory toEntity(UserDonationHistoryDto dto, Account account, DonationEvent event, EventRegistration registration) {
        if (dto == null) {
            return null;
        }

        UserDonationHistory userDonationHistory = new UserDonationHistory();
        userDonationHistory.setId(dto.getId());
        userDonationHistory.setAccount(account);
        userDonationHistory.setEvent(event);
        userDonationHistory.setRegistration(registration);

        return userDonationHistory;
    }
}
