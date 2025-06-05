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
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private DonationEventValidator validator;

    @Transactional
    public String createDonation(DonationEventDto donationEventDto, String staffEmail) {
        // Validate Input
        validator.validateStaffAccess(staffEmail, "create donation events");
        validator.validateEventCreation(donationEventDto);

        // Fetch Data
        User staff = userRepository.findByEmail(staffEmail);

        // Create And Save Donation Event
        DonationEvent donationEvent = new DonationEvent();
        donationEvent.setName(donationEventDto.getName());
        donationEvent.setLocation(donationEventDto.getLocation());
        donationEvent.setDonationDate(donationEventDto.getDonationDate());
        donationEvent.setTotalMemberCount(donationEventDto.getTotalMemberCount());
        donationEvent.setStatus(donationEventDto.getStatus());
        donationEvent.setDonationType(donationEventDto.getDonationType());
        donationEvent.setUser(staff);
        donationEvent.setCreatedDate(LocalDate.now());
        DonationEvent savedDonationEvent = donationEventRepository.save(donationEvent);

        // Create QR code for the event
        try {
            String qrContent = String.format("http://localhost:8080/donation-events/%d/check-in", savedDonationEvent.getId());
            byte[] qrCodeImage = qrCodeService.generateQRCode(qrContent);
            savedDonationEvent.setQrCode(qrCodeImage);
            donationEventRepository.save(savedDonationEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code " + e.getMessage());
        }

        // Create time slots for the event
        List<DonationTimeSlot> timeSlots = donationTimeSlotService.createTimeSlotsForEvent(donationEventDto.getTimeSlotDtos(), savedDonationEvent);
        savedDonationEvent.setTimeSlots(timeSlots);
        return "Donation event created successfully";
    }

    @Transactional
    public String verifyDonationEvent(Long eventId, String adminEmail, String action) {
        // Validate Input
        validator.validateAdminAccess(adminEmail, "verify donation events");
        validator.validateEventVerification(action);

        // Fetch Data
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        User admin = userRepository.findByEmail(adminEmail);

        // Update Event Status
        donationEvent.setStatus(action.equals("approve") ? Status.APPROVED : Status.REJECTED);
        donationEvent.setUser(admin);
        donationEventRepository.save(donationEvent);

        return "Donation event " + action + "d successfully";
    }

    @Transactional
    public String registerForEvent(Long eventId, Long timeSlotId, String userEmail) {
        // Validate Input
        validator.validateMemberAccess(userEmail, "register for donation events");

        //Fetch Data
        User user = userRepository.findByEmail(userEmail);
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        DonationTimeSlot timeSlot = validator.getSlotOrThrow(timeSlotId);

        // Validate Registration Eligibility
        validator.validateRegistrationEligibility(user, donationEvent, timeSlot);

        // Create And Save Registration
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
        // Fetch Data
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);

        return DonationEventMapper.toDto(donationEvent);
    }

    public List<DonationEventDto> getAllDonationEvents() {
        // Fetch Data
        List<DonationEvent> donationEvents = donationEventRepository.findAll();

        return donationEvents.stream()
                .map(DonationEventMapper::toDto)
                .toList();
    }

    public List<DonationEventDto> getEventByBetweenDates(LocalDate startDate, LocalDate endDate) {
        // Fetch Data
        List<DonationEvent> donationEvents = donationEventRepository.findByDonationDateBetween(startDate, endDate);

        return donationEvents.stream()
                .map(DonationEventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String recordMultipleBloodDonations(Long eventId, List<SingleBloodUnitRecordDto> records, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail, "record blood donations");

        // Fetch Data
        DonationEvent event = validator.getEventOrThrow(eventId);
        validator.validateBloodDonationRecording(event, records);

        for (SingleBloodUnitRecordDto record : records) {
            User donor = validator.getDonorOrThrow(record.getUserId());
            recordSingleBloodDonation(record, event, donor);
        }

        event.setStatus(Status.COMPLETED);
        donationEventRepository.save(event);
        return String.format("Successfully recorded %d blood donation(s)", records.size());
    }

    public List<UserDto> getEventDonors(Long eventId, Long timeSlotId, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail, "donors of blood donations");

        // Fetch Data
        User user = userRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);
        DonationTimeSlot timeSlot = validator.getSlotOrThrow(timeSlotId);

        List<EventRegistration> registrations = eventRegistrationRepository.findByEventAndTimeSlot(event, timeSlot);
        return registrations.stream()
                .map(EventRegistration::getUser)     // Get User from each registration
                .map(UserMapper::mapToUserDto)       // Convert each User to UserDto
                .collect(Collectors.toList());
    }

    @Transactional
    public String checkInMember(Long eventId, String registrationId, String userEmail) {
        // Validate Input
        validator.validateMemberAccess(userEmail, "check-in to donation events");
        // Fetch Data
        User member = userRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = validator.getRegistrationOrThrow(registrationId);
        validator.validateCheckIn(event, registration, member);

        registration.setStatus(Status.CHECKED_IN);
        eventRegistrationRepository.save(registration);
        return "Checked-in successfully";
    }

    private void recordSingleBloodDonation(SingleBloodUnitRecordDto record, DonationEvent event, User donor) {
        EventRegistration registration = eventRegistrationRepository.findByEventAndUser(event, donor)
                .orElseThrow(() -> new RuntimeException(String.format("User %s is not registered for this event", donor.getId())));
        if (registration.getStatus() != Status.CHECKED_IN) {
            throw new RuntimeException(String.format("User %s is not checked in for this event", donor.getId()));
        }

        BloodUnit bloodUnit = new BloodUnit();
        bloodUnit.setEvent(event);
        bloodUnit.setDonor(donor);
        bloodUnit.setVolume(record.getVolume());
        bloodUnit.setBloodType(donor.getBloodType());
        bloodUnit.setStatus(Status.PENDING);

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
}
