package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.DonationTimeSlotDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationTimeSlot;
import org.springframework.stereotype.Component;

@Component
public class DonationTimeSlotMapper {
    public DonationTimeSlotDto toDto(DonationTimeSlot timeSlot) {
        if (timeSlot == null) return null;

        DonationTimeSlotDto dto = new DonationTimeSlotDto();
        dto.setId(timeSlot.getId());
        dto.setStartTime(timeSlot.getStartTime());
        dto.setEndTime(timeSlot.getEndTime());
        dto.setMaxCapacity(timeSlot.getMaxCapacity());
        dto.setCurrentRegistrations(timeSlot.getCurrentRegistrations());
        dto.setEventId(timeSlot.getEvent().getId());

        return dto;
    }

    public DonationTimeSlot toEntity(DonationTimeSlotDto dto, DonationEvent event) {
        if (dto == null) return null;

        DonationTimeSlot timeSlot = new DonationTimeSlot();
        timeSlot.setId(dto.getId());
        timeSlot.setStartTime(dto.getStartTime());
        timeSlot.setEndTime(dto.getEndTime());
        timeSlot.setMaxCapacity(dto.getMaxCapacity());
        timeSlot.setCurrentRegistrations(0);
        timeSlot.setEvent(event);

        return timeSlot;
    }
}
