package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventMapper;
import com.blooddonation.blood_donation_support_system.repository.DonationEventRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DonationEventService {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private UserRepository userRepository;

    public String createDonation (DonationEventDto donationEventDto, String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail);
        if (staff == null) {
            throw new RuntimeException("Staff member not found with email: " + staffEmail);
        }

        DonationEvent donationEvent = new DonationEvent();
        donationEvent.setName(donationEventDto.getName());
        donationEvent.setLocation(donationEventDto.getLocation());
        donationEvent.setDonationDate(donationEventDto.getDonationDate());
        donationEvent.setStartTime(donationEventDto.getStartTime());
        donationEvent.setEndTime(donationEventDto.getEndTime());
        donationEvent.setTotalMemberCount(donationEventDto.getTotalMemberCount());
        donationEvent.setStatus(donationEventDto.getStatus());
        donationEvent.setUser(staff); // Set the staff member as the creator of the event
        donationEvent.setCreatedDate(LocalDate.now());

        DonationEvent savedDonationEvent = donationEventRepository.save(donationEvent);
        return "Donation event created successfully";
    }

    public String approveDonationEvent(Long eventId, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null) {
            throw new RuntimeException("Admin not found with email: " + adminEmail);
        }

        DonationEvent donationEvent = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        donationEvent.setStatus(Status.APPROVED);
        donationEvent.setUser(admin);
        donationEventRepository.save(donationEvent);

        return "Donation event approved successfully";
    }
}
