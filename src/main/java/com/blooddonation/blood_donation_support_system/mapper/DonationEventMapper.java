package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.DonationTimeSlotDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationTimeSlot;
import com.blooddonation.blood_donation_support_system.enums.DonationEventStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DonationEventMapper {

    public static DonationEventDto toDto(DonationEvent event) {
        if (event == null) return null;

        List<DonationTimeSlotDto> slots = null;
        if (event.getTimeSlots() != null) {
            slots = event.getTimeSlots().stream()
                    .map(DonationTimeSlotMapper::toDto)
                    .collect(Collectors.toList());
        }

        return DonationEventDto.builder()
                .id(event.getId())
                .name(event.getName())
                .hospital(event.getHospital())
                .address(event.getAddress())
                .ward(event.getWard())
                .district(event.getDistrict())
                .city(event.getCity())
                .organizer(event.getOrganizer())
                .donationDate(event.getDonationDate())
                .registeredMemberCount(0) // Default value
                .totalMemberCount(event.getTotalMemberCount())
                .status(event.getStatus())
                .donationType(event.getDonationType())
                .accountId(event.getAccount() != null ? event.getAccount().getId() : null)
                .createdDate(LocalDate.now())
                .timeSlotDtos(slots) // Set the timeSlotDtos here
                .build();
    }


    public static DonationEvent toEntity(DonationEventDto dto, Account account) {
        if (dto == null) return null;

        return DonationEvent.builder()
                .id(dto.getId())
                .name(dto.getName())
                .hospital(dto.getHospital())
                .address(dto.getAddress())
                .ward(dto.getWard())
                .district(dto.getDistrict())
                .city(dto.getCity())
                .organizer(dto.getOrganizer())
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
                .hospital(dto.getHospital())
                .address(dto.getAddress())
                .ward(dto.getWard())
                .district(dto.getDistrict())
                .city(dto.getCity())
                .organizer(dto.getOrganizer())
                .donationDate(dto.getDonationDate())
                .registeredMemberCount(0) // Default value
                .totalMemberCount(dto.getTotalMemberCount())
                .status(DonationEventStatus.AVAILABLE)
                .donationType(dto.getDonationType())
                .account(account)
                .createdDate(LocalDate.now())
                .build();
    }

    public static DonationEvent updateDonation(DonationEventDto dto, Account account, Long originalId) {
        if (dto == null) return null;

        DonationEvent event = DonationEvent.builder()
                .id(originalId)
                .name(dto.getName())
                .hospital(dto.getHospital())
                .address(dto.getAddress())
                .ward(dto.getWard())
                .district(dto.getDistrict())
                .city(dto.getCity())
                .organizer(dto.getOrganizer())
                .donationDate(dto.getDonationDate())
                .registeredMemberCount(0)
                .totalMemberCount(dto.getTotalMemberCount())
                .status(DonationEventStatus.AVAILABLE)
                .donationType(dto.getDonationType())
                .account(account)
                .createdDate(LocalDate.now())
                .build();

        if (dto.getTimeSlotDtos() != null) {
            List<DonationTimeSlot> timeSlots = dto.getTimeSlotDtos().stream()
                    .map(slotDto -> DonationTimeSlotMapper.toEntity(slotDto, event))
                    .collect(Collectors.toList());
            event.setTimeSlots(timeSlots);
        }

        return event;
    }
}