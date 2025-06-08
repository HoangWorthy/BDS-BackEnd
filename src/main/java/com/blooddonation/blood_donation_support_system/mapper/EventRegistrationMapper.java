package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.EventRegistrationDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EventRegistrationMapper {

    public static EventRegistrationDto toDto(EventRegistration registration) {
        if (registration == null) return null;

        EventRegistrationDto dto = new EventRegistrationDto();
        dto.setId(registration.getId());
        dto.setAccountId(registration.getAccount() != null ? registration.getAccount().getId() : null);
        dto.setEventId(registration.getEvent() != null ? registration.getEvent().getId() : null);
        dto.setBloodType(registration.getBloodType());
        dto.setDonationType(registration.getDonationType());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setStatus(registration.getStatus());
        dto.setQrCode(registration.getQrCode());

        return dto;
    }

    public static EventRegistration toEntity(EventRegistrationDto dto, Account account, DonationEvent event) {
        if (dto == null) return null;

        EventRegistration registration = new EventRegistration();
        registration.setId(dto.getId());
        registration.setAccount(account);   // Must be fetched before mapping
        registration.setEvent(event); // Must be fetched before mapping
        registration.setBloodType(dto.getBloodType());
        registration.setDonationType(dto.getDonationType());
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setStatus(dto.getStatus());
        registration.setQrCode(dto.getQrCode());

        return registration;
    }
}
