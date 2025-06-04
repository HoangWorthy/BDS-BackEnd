package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DonationTimeSlotDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationTimeSlot;
import com.blooddonation.blood_donation_support_system.repository.DonationTimeSlotRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationTimeSlotService {
    @Autowired
    private DonationTimeSlotRepository donationTimeSlotRepository;

    @Transactional
    public List<DonationTimeSlot> createTimeSlotsForEvent(List<DonationTimeSlotDto> donationTimeSlotDto, DonationEvent event) {
        if (donationTimeSlotDto == null || donationTimeSlotDto.isEmpty()) {
            return List.of();
        }

        // Calculate total capacity for all slots
        int totalCapacity = donationTimeSlotDto.stream()
                .mapToInt(DonationTimeSlotDto::getMaxCapacity)
                .sum();

        if (totalCapacity != event.getTotalMemberCount()) {
            throw new RuntimeException("Total capacity of time slots (" + totalCapacity +
                    ") must equal event's total member count (" + event.getTotalMemberCount() + ")");
        }

        List<DonationTimeSlot> timeSlots = donationTimeSlotDto.stream()
                .map(slotDto -> {
                    DonationTimeSlot timeSlot = new DonationTimeSlot();
                    timeSlot.setStartTime(slotDto.getStartTime());
                    timeSlot.setEndTime(slotDto.getEndTime());
                    timeSlot.setMaxCapacity(slotDto.getMaxCapacity());
                    timeSlot.setCurrentRegistrations(0);
                    timeSlot.setEvent(event);
                    return timeSlot;
                })
                .collect(Collectors.toList());

        return donationTimeSlotRepository.saveAll(timeSlots);
    }
}
