package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BloodUnitDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.BloodUnitMapper;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventMapper;
import com.blooddonation.blood_donation_support_system.mapper.UserMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.DonationEventRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationEventService {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    public String createDonation(DonationEventDto donationEventDto, String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail);
        if (staff == null) {
            throw new RuntimeException("Staff does not exist");
        }
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff members can create donation events");
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

    public String verifyDonationEvent(Long eventId, String adminEmail, String action) {
        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null) {
            throw new RuntimeException("Admin does not exist");
        }
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can approve donation events");
        }

        DonationEvent donationEvent = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        if (action.equals("approve")) {
            donationEvent.setStatus(Status.APPROVED);
        } else if (action.equals("reject")) {
            donationEvent.setStatus(Status.REJECTED);
        } else {
            throw new RuntimeException("Invalid action: " + action);
        }
        donationEvent.setUser(admin);
        donationEventRepository.save(donationEvent);

        return "Donation event approved successfully";
    }

    @Transactional
    public String registerForEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        if (!user.getRole().equals(Role.MEMBER)) {
            throw new RuntimeException("Only members can register for donation events");
        }

        DonationEvent donationEvent = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        if (donationEvent.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Cannot register for an event that is not approved");
        }

        // Check capacity
        if (donationEvent.getRegisteredMemberCount() >= donationEvent.getTotalMemberCount()) {
            throw new RuntimeException("Event has reached maximum capacity");
        }

        // Create registration
        EventRegistration registration = new EventRegistration();
        registration.setUser(user);
        registration.setEvent(donationEvent);
        registration.setBloodType(user.getBloodType());
        registration.setStatus(Status.PENDING);

        eventRegistrationRepository.save(registration);
        return "Registration successful";
    }

    public DonationEventDto getDonationEventById(Long eventId) {
        DonationEvent donationEvent = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));
        return DonationEventMapper.toDto(donationEvent);
    }

    public List<DonationEventDto> getAllDonationEvents() {
        List<DonationEvent> donationEvents = donationEventRepository.findAll();
        return donationEvents.stream()
                .map(DonationEventMapper::toDto)
                .toList();
    }

//    @Transactional
    public String recordMultipleBloodDonations(Long eventId, List<SingleBloodUnitRecordDto> records) {
        DonationEvent event = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        int successCount = 0;
        List<Long> failedUserIds = new ArrayList<>();

        for (SingleBloodUnitRecordDto record : records) {
            try {
                User donor = userRepository.findById(record.getUserId())
                        .orElseThrow(() -> new RuntimeException("Donor not found with id: " + record.getUserId()));

                EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, donor)
                        .orElseThrow(() -> new RuntimeException("User not registered for this event"));

                BloodUnit bloodUnit = new BloodUnit();
                bloodUnit.setEvent(event);
                bloodUnit.setDonor(donor);
                bloodUnit.setVolume(record.getVolume());
                bloodUnit.setBloodType(donor.getBloodType());

                bloodUnitRepository.save(bloodUnit);
                successCount++;
            } catch (RuntimeException e) {
                failedUserIds.add(record.getUserId());
            }
        }

        return successCount + " blood donations recorded successfully. " +
                (failedUserIds.isEmpty()
                        ? ""
                        : "Failed for user IDs: " + failedUserIds);
    }


    public List<UserDto> getEventDonors(Long eventId) {
        DonationEvent event = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        List<EventRegistration> registrations = eventRegistrationRepository.findByEvent(event);
        return registrations.stream()
                .map(EventRegistration::getUser)     // Get User from each registration
                .map(UserMapper::mapToUserDto)       // Convert each User to UserDto
                .collect(Collectors.toList());
    }
}
