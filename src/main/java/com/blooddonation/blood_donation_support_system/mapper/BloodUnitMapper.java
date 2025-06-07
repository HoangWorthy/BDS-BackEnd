package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BloodUnitMapper {
    public static BloodUnitDto toDto(BloodUnit entity) {
        if (entity == null) return null;

        BloodUnitDto dto = new BloodUnitDto();
        dto.setId(entity.getId());
        dto.setEventId(entity.getEvent() != null ? entity.getEvent().getId() : null);
        dto.setAccountId(entity.getDonor() != null ? entity.getDonor().getId() : null);
        dto.setBloodType(entity.getBloodType());
        dto.setComponentType(entity.getComponentType());
        dto.setVolume(entity.getVolume());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public static BloodUnit toEntity(BloodUnitDto dto, Account donor, DonationEvent event) {
        if (dto == null) return null;

        BloodUnit entity = new BloodUnit();
        entity.setId(dto.getId());
        entity.setDonor(donor);
        entity.setEvent(event);
        entity.setBloodType(dto.getBloodType());
        entity.setComponentType(dto.getComponentType());
        entity.setVolume(dto.getVolume());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}