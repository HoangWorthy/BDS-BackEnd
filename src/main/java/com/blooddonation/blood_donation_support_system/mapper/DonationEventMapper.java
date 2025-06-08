package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DonationEventMapper {


    public static DonationEventDto toDto(DonationEvent event) {
        if (event == null) return null;

        DonationEventDto dto = new DonationEventDto();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setLocation(event.getLocation());
        dto.setDonationDate(event.getDonationDate());
        dto.setRegisteredMemberCount(0);
        dto.setTotalMemberCount(event.getTotalMemberCount());
        dto.setStatus(event.getStatus());
        dto.setDonationType(event.getDonationType());
        dto.setAccountId(event.getAccount() != null ? event.getAccount().getId() : null);
        dto.setCreatedDate(dto.getDonationDate());

        return dto;
    }

    public static DonationEvent toEntity(DonationEventDto dto, Account account) {
        if (dto == null) return null;

        DonationEvent event = new DonationEvent();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setDonationDate(dto.getDonationDate());
        event.setRegisteredMemberCount(0);
        event.setTotalMemberCount(dto.getTotalMemberCount());
        event.setStatus(dto.getStatus());
        event.setDonationType(dto.getDonationType());
        event.setAccount(account); // Must be resolved from service before mapping
        event.setCreatedDate(LocalDate.now());

        return event;
    }
}