package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DonationEventMapper {

    public static DonationEventDto toDto(DonationEvent event) {
        if (event == null) return null;

        return DonationEventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .location(event.getLocation())
                .donationDate(event.getDonationDate())
                .registeredMemberCount(0) // Default value
                .totalMemberCount(event.getTotalMemberCount())
                .status(event.getStatus())
                .donationType(event.getDonationType())
                .accountId(event.getAccount() != null ? event.getAccount().getId() : null)
                .createdDate(event.getDonationDate()) // Using donationDate as createdDate
                .build();
    }

    public static DonationEvent toEntity(DonationEventDto dto, Account account) {
        if (dto == null) return null;

        return DonationEvent.builder()
                .id(dto.getId())
                .name(dto.getName())
                .location(dto.getLocation())
                .donationDate(dto.getDonationDate())
                .registeredMemberCount(0) // Default value
                .totalMemberCount(dto.getTotalMemberCount())
                .status(dto.getStatus())
                .donationType(dto.getDonationType())
                .account(account)
                .createdDate(LocalDate.now())
                .build();
    }

    public static DonationEvent createDonation(DonationEventDto dto, Account account) {
        if (dto == null) return null;

        return DonationEvent.builder()
                .id(dto.getId())
                .name(dto.getName())
                .location(dto.getLocation())
                .donationDate(dto.getDonationDate())
                .registeredMemberCount(0) // Default value
                .totalMemberCount(dto.getTotalMemberCount())
                .status(Status.PENDING)
                .donationType(dto.getDonationType())
                .account(account)
                .createdDate(LocalDate.now())
                .build();
    }
}