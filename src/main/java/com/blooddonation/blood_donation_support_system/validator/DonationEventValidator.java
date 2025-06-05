package com.blooddonation.blood_donation_support_system.validator;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.DonationEventRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.DonationTimeSlotRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import com.blooddonation.blood_donation_support_system.service.QRCodeService;
import com.blooddonation.blood_donation_support_system.service.DonationTimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DonationEventValidator {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationTimeSlotRepository donationTimeSlotRepository;

    @Autowired
    private QRCodeService qrCodeService;

    public void validateStaffAccess(String email, String operation) {
        User staff = userRepository.findByEmail(email);
        if (staff == null) {
            throw new RuntimeException("Staff does not exist");
        }
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff can " + operation);
        }
    }

    public void validateMemberAccess(String email, String operation) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("member does not exist");
        }
        if (!user.getRole().equals(Role.MEMBER)) {
            throw new RuntimeException("Only member can " + operation);
        }
    }

    public void validateAdminAccess(String email, String operation) {
        User admin = userRepository.findByEmail(email);
        if (admin == null) {
            throw new RuntimeException("Admin does not exist");
        }
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admin can " + operation);
        }
    }

    public DonationEvent getEventOrThrow(Long eventId) {
        return donationEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Donation event not found with id: " + eventId));
    }

    public DonationTimeSlot getSlotOrThrow(Long timeSlotId) {
        return donationTimeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found with id: " + timeSlotId));
    }

    public User getDonorOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Donor not found with id: " + userId));
    }

    public EventRegistration getRegistrationOrThrow(String registrationId) {
        return eventRegistrationRepository.findById(Long.parseLong(registrationId))
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));
    }

    public void validateEventVerification(String action) {
        if (!action.equals("approve") && !action.equals("reject")) {
            throw new RuntimeException("Invalid action: " + action);
        }
    }

    public void validateEventCreation(DonationEventDto dto) {
        if (dto.getDonationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Donation date cannot be in the past");
        }
    }

    public void validateBloodDonationRecording(DonationEvent event, List<SingleBloodUnitRecordDto> records) {
        if (event.getStatus().equals(Status.COMPLETED)) {
            throw new RuntimeException("Event is already recorded");
        }

        if (records == null || records.isEmpty()) {
            throw new RuntimeException("Blood donation records cannot be empty");
        }
    }

    public void validateRegistrationEligibility(User user, DonationEvent event, DonationTimeSlot timeSlot) {
        // Validate member access
        validateMemberAccess(user.getEmail(), "register for donation events");

        // Check event status
        if (event.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Cannot register for an event that is not approved");
        }

        // Check event capacity
        if (event.getRegisteredMemberCount() >= event.getTotalMemberCount()) {
            throw new RuntimeException("Event has reached maximum capacity");
        }

        // Validate time slot belongs to event
        if (!timeSlot.getEvent().getId().equals(event.getId())) {
            throw new RuntimeException("Time slot does not belong to this event");
        }

        // Check duplicate registration
        if (eventRegistrationRepository.existsByUserAndEvent(user, event)) {
            throw new RuntimeException("You have already registered for this event");
        }

        // Check donation eligibility
        if (user.getNextEligibleDonationDate() != null &&
                user.getNextEligibleDonationDate().isAfter(event.getDonationDate())) {
            throw new RuntimeException("You are not eligible to donate on this date. Please check your next eligible donation date.");
        }
    }

    public void validateCheckIn(DonationEvent event, EventRegistration registration, User member) {
        if (!registration.getEvent().getId().equals(event.getId())) {
            throw new RuntimeException("Registration does not belong to this event");
        }
        if (!registration.getUser().getId().equals(member.getId())) {
            throw new RuntimeException("Registration does not belong to this user");
        }
        if (registration.getStatus() == Status.CHECKED_IN) {
            throw new RuntimeException("User is already checked-in for this event");
        }
    }
}
