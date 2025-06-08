package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.EventRegistrationDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.Status;
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

    public static EventRegistration toEntity(EventRegistrationDto dto, Account account, DonationEvent event, DonationTimeSlot donationTimeSlot, Profile profile) {
        if (dto == null) return null;

        EventRegistration registration = new EventRegistration();
        registration.setId(dto.getId());
        registration.setAccount(account);   // Must be fetched before mapping
        registration.setEvent(event); // Must be fetched before mapping
        registration.setTimeSlot(donationTimeSlot);
        registration.setBloodType(profile.getBloodType());
        registration.setDonationType(event.getDonationType());
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setStatus(Status.PENDING);
        registration.setQrCode(dto.getQrCode());

        return registration;
    }

    public static EventRegistration registerOfflineEntity(DonationEvent event, Account account, Profile profile) {
        if (event == null) return null;
        EventRegistration registration = new EventRegistration();
        registration.setAccount(account);
        registration.setEvent(event);
        registration.setDonationType(event.getDonationType());
        registration.setBloodType(profile.getBloodType());
        registration.setStatus(Status.APPROVED);

        return registration;
    }
}
