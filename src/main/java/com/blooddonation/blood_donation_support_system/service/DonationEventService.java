package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventMapper;
import com.blooddonation.blood_donation_support_system.mapper.UserMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.DonationEventRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.DonationTimeSlotRepository;
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

    @Autowired
    private DonationTimeSlotService donationTimeSlotService;

    @Autowired
    private DonationTimeSlotRepository donationTimeSlotRepository;

    @Transactional
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
        donationEvent.setTotalMemberCount(donationEventDto.getTotalMemberCount());
        donationEvent.setStatus(donationEventDto.getStatus());
        donationEvent.setDonationType(donationEventDto.getDonationType());
        donationEvent.setUser(staff); // Set the staff member as the creator of the event
        donationEvent.setCreatedDate(LocalDate.now());

        DonationEvent savedDonationEvent = donationEventRepository.save(donationEvent);

        // Create time slots for the event
        List<DonationTimeSlot> timeSlots = donationTimeSlotService.createTimeSlotsForEvent(donationEventDto.getTimeSlotDtos(), savedDonationEvent);
        savedDonationEvent.setTimeSlots(timeSlots);
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
    public String registerForEvent(Long eventId, Long timeSlotId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        if (!user.getRole().equals(Role.MEMBER)) {
            throw new RuntimeException("Only members can register for donation events");
        }

        DonationEvent donationEvent = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        DonationTimeSlot timeSlot = donationTimeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found with id: " + timeSlotId));

        // Check if the event is approved
        if (donationEvent.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Cannot register for an event that is not approved");
        }

        // Check capacity
        if (donationEvent.getRegisteredMemberCount() >= donationEvent.getTotalMemberCount()) {
            throw new RuntimeException("Event has reached maximum capacity");
        }

        // Validate if time slot belongs to the event
        if (!timeSlot.getEvent().getId().equals(eventId)) {
            throw new RuntimeException("Time slot does not belong to this event");
        }

        // Check if user already registered
        if (eventRegistrationRepository.existsByUserAndEvent((user), donationEvent)) {
            throw new RuntimeException("You have already registered for this event");
        }

        // Check if user is eligible to donate
        if(user.getNextEligibleDonationDate() != null &&
           user.getNextEligibleDonationDate().isAfter(donationEvent.getDonationDate())) {
            throw new RuntimeException("You are not eligible to donate on this date. Please check your next eligible donation date.");
        }

        // Create registration
        EventRegistration registration = new EventRegistration();
        registration.setUser(user);
        registration.setEvent(donationEvent);
        registration.setTimeSlot(timeSlot);
        registration.setBloodType(user.getBloodType());
        registration.setDonationType(donationEvent.getDonationType());
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

    @Transactional
    public String recordMultipleBloodDonations(Long eventId, List<SingleBloodUnitRecordDto> records, String userEmail) {
        User staff = userRepository.findByEmail(userEmail);
        if (staff == null) {
            throw new RuntimeException("User does not exist");
        }
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff members can record blood donations");
        }

        DonationEvent event = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));

        if (event.getStatus().equals(Status.COMPLETED)) {
            throw new RuntimeException("Event is already recorded");
        }
        // Validate all records first
        List<String> errors = new ArrayList<>();
        for (SingleBloodUnitRecordDto record : records) {
            try {
                User donor = userRepository.findById(record.getUserId())
                        .orElseThrow(() -> new RuntimeException("Donor not found with id: "));

                EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, donor)
                        .orElseThrow(() -> new RuntimeException("User not registered for this event"));
            } catch (RuntimeException e) {
                errors.add(String.format("Donor #%d - %s", record.getUserId(), e.getMessage()));
            }
        }

        // If there are any errors, throw exception to rollback transaction
        if (!errors.isEmpty()) {
            StringBuilder result = new StringBuilder();
            result.append("Cannot record donations due to following errors:");
            for (String error : errors) {
                result.append("\n• ").append(error);
            }
            throw new RuntimeException(result.toString());
        }

        // If no errors, proceed with all records
        for (SingleBloodUnitRecordDto record : records) {
            User donor = userRepository.findById(record.getUserId()).get();
            EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, donor).get();

            BloodUnit bloodUnit = new BloodUnit();
            bloodUnit.setEvent(event);
            bloodUnit.setDonor(donor);
            bloodUnit.setVolume(record.getVolume());
            bloodUnit.setBloodType(donor.getBloodType());
            if (event.getDonationType().equals(DonationType.WHOLE_BLOOD)) {
                bloodUnit.setComponentType(ComponentType.WHOLE_BLOOD);
                donor.setNextEligibleDonationDate(event.getDonationDate().plusWeeks(12));
            } else {
                bloodUnit.setComponentType(ComponentType.PLATELETS);
                donor.setNextEligibleDonationDate(event.getDonationDate().plusWeeks(3));
            }
            donor.setLastDonationDate(event.getDonationDate());
            userRepository.save(donor);

            bloodUnitRepository.save(bloodUnit);
            registration.setStatus(Status.COMPLETED);
            eventRegistrationRepository.save(registration);
        }
        event.setStatus(Status.COMPLETED);
        donationEventRepository.save(event);
        return String.format("Successfully recorded %d blood donation(s)", records.size());
    }

    public List<UserDto> getEventDonors(Long eventId, Long timeSlotId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        if (!user.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff members can view event donors");
        }

        DonationEvent event = donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));
        DonationTimeSlot timeSlot = donationTimeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found with id: " + timeSlotId));

        List<EventRegistration> registrations = eventRegistrationRepository.findByEventAndTimeSlot(event, timeSlot);
        return registrations.stream()
                .map(EventRegistration::getUser)     // Get User from each registration
                .map(UserMapper::mapToUserDto)       // Convert each User to UserDto
                .collect(Collectors.toList());
    }
}
