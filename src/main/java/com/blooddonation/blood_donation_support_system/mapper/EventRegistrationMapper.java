package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.EventRegistrationDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EventRegistrationMapper {

    public static EventRegistrationDto toDto(EventRegistration registration) {
        if (registration == null) return null;

        EventRegistrationDto dto = new EventRegistrationDto();
        dto.setId(registration.getId());
        dto.setUserId(registration.getUser() != null ? registration.getUser().getId() : null);
        dto.setEventId(registration.getEvent() != null ? registration.getEvent().getId() : null);
        dto.setBloodType(registration.getBloodType());
        dto.setDonationType(registration.getDonationType());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setStatus(registration.getStatus());

        return dto;
    }

    public static EventRegistration toEntity(EventRegistrationDto dto, User user, DonationEvent event) {
        if (dto == null) return null;

        EventRegistration registration = new EventRegistration();
        registration.setId(dto.getId());
        registration.setUser(user);   // Must be fetched before mapping
        registration.setEvent(event); // Must be fetched before mapping
        registration.setBloodType(dto.getBloodType());
        registration.setDonationType(dto.getDonationType());
        registration.setRegistrationDate(dto.getRegistrationDate());
        registration.setStatus(dto.getStatus());

        return registration;
    }
}
