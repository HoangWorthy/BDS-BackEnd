package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DonationEventMapper {


    public static DonationEventDto toDto(DonationEvent event) {
        if (event == null) return null;

        DonationEventDto dto = new DonationEventDto();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setLocation(event.getLocation());
        dto.setDonationDate(event.getDonationDate());
        dto.setRegisteredMemberCount(event.getRegisteredMemberCount());
        dto.setTotalMemberCount(event.getTotalMemberCount());
        dto.setStatus(event.getStatus());
        dto.setDonationType(event.getDonationType());
        dto.setUserId(event.getUser() != null ? event.getUser().getId() : null);
        dto.setCreatedDate(event.getCreatedDate());

        return dto;
    }

    public static DonationEvent toEntity(DonationEventDto dto, User user) {
        if (dto == null) return null;

        DonationEvent event = new DonationEvent();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setDonationDate(dto.getDonationDate());
        event.setRegisteredMemberCount(dto.getRegisteredMemberCount());
        event.setTotalMemberCount(dto.getTotalMemberCount());
        event.setStatus(dto.getStatus());
        event.setDonationType(dto.getDonationType());
        event.setUser(user); // Must be resolved from service before mapping
        event.setCreatedDate(dto.getCreatedDate());

        return event;
    }
}