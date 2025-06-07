package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventMapper;
import com.blooddonation.blood_donation_support_system.repository.*;
import com.blooddonation.blood_donation_support_system.service.DonationEventService;
import com.blooddonation.blood_donation_support_system.service.DonationTimeSlotService;
import com.blooddonation.blood_donation_support_system.service.QRCodeService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DonationEventServiceImpl implements DonationEventService {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Autowired
    private DonationTimeSlotService donationTimeSlotService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private DonationEventValidator validator;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private DonationTimeSlotRepository donationTimeSlotRepository;

    @Transactional
    public String createDonation(DonationEventDto donationEventDto, String staffEmail) {
        // Validate Input
        validator.validateStaffAccess(staffEmail, "create donation events");
        validator.validateEventCreation(donationEventDto);

        // Fetch Data
        Account staff = accountRepository.findByEmail(staffEmail);

        // Create And Save Donation Event
        DonationEvent donationEvent = new DonationEvent();
        donationEvent.setName(donationEventDto.getName());
        donationEvent.setLocation(donationEventDto.getLocation());
        donationEvent.setDonationDate(donationEventDto.getDonationDate());
        donationEvent.setTotalMemberCount(donationEventDto.getTotalMemberCount());
        donationEvent.setStatus(donationEventDto.getStatus());
        donationEvent.setDonationType(donationEventDto.getDonationType());
        donationEvent.setAccount(staff);
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
        Account admin = accountRepository.findByEmail(adminEmail);

        // Update Event Status
        donationEvent.setStatus(action.equals("approve") ? Status.APPROVED : Status.REJECTED);
        donationEvent.setAccount(admin);
        donationEventRepository.save(donationEvent);

        return "Donation event " + action + "d successfully";
    }

    @Transactional
    public String registerForEventOnline(Long eventId, Long timeSlotId, String userEmail) {
        // Validate Input
        validator.validateMemberAccess(userEmail, "register for donation events");

        //Fetch Data
        Account account = accountRepository.findByEmail(userEmail);
        Profile profile = account.getProfile();
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        DonationTimeSlot timeSlot = validator.getSlotOrThrow(timeSlotId);

        // Validate Registration Eligibility
        validator.validateRegistrationEligibility(account, donationEvent, timeSlot);

        // Create And Save Registration
        EventRegistration registration = new EventRegistration();
        registration.setAccount(account);
        registration.setEvent(donationEvent);
        registration.setTimeSlot(timeSlot);
        registration.setBloodType(profile.getBloodType());
        registration.setDonationType(donationEvent.getDonationType());
        registration.setStatus(Status.PENDING);
        eventRegistrationRepository.save(registration);

        return "Registration successful";
    }

    @Transactional
    public String registerForEventOffline(Long eventId, String personalId, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail, "register for donation events");

        // Fetch Data
        DonationEvent event = validator.getEventOrThrow(eventId);
        Account member = validator.validateAndGetMemberAccount(personalId);
        EventRegistration registration = validator.validateAndGetExistingRegistration(member, event);
        if (registration != null) {
            registration.setStatus(Status.CHECKED_IN);
            eventRegistrationRepository.save(registration);
            return "Member checked in successfully";
        }

        validator.validateRegistrationEligibility(member, event, null);
        EventRegistration newRegistration = validator.createRegistration(member, event);
        eventRegistrationRepository.save(newRegistration);
        return "Member registered and checked in successfully";
    }

    @Transactional
    public String cancelEventRegistration(Long eventId, String userEmail) {
        // Validate Input
        validator.validateMemberAccess(userEmail, "cancel event registration");

        // Fetch Data
        Account member = accountRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = eventRegistrationRepository.findByEventAndAccount(event, member)
                .orElseThrow(() -> new RuntimeException("No registration found for this event"));

        // Validate cancellation eligibility
        validator.validateCancellation(event, registration);

        // Update registration status and decrease counts
        registration.setStatus(Status.CANCELLED);
        eventRegistrationRepository.save(registration);
        event.setRegisteredMemberCount(event.getRegisteredMemberCount() - 1);
        donationEventRepository.save(event);
        DonationTimeSlot timeSlot = registration.getTimeSlot();
        if (timeSlot != null) {
            timeSlot.setCurrentRegistrations(timeSlot.getCurrentRegistrations() - 1);
            donationTimeSlotRepository.save(timeSlot);
        }

        return "Registration cancelled successfully";
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
            Account donor = validator.getDonorOrThrow(record.getAccountId());
            recordSingleBloodDonation(record, event, donor);
        }

        event.setStatus(Status.COMPLETED);
        donationEventRepository.save(event);
        return String.format("Successfully recorded %d blood donation(s)", records.size());
    }

    public List<AccountDto> getEventDonors(Long eventId, Long timeSlotId, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail, "donors of blood donations");

        // Fetch Data
        Account account = accountRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);
        DonationTimeSlot timeSlot = validator.getSlotOrThrow(timeSlotId);

        List<EventRegistration> registrations = eventRegistrationRepository.findByEventAndTimeSlot(event, timeSlot);
        return registrations.stream()
                .map(EventRegistration::getAccount)     // Get User from each registration
                .map(AccountMapper::toDto)       // Convert each User to UserDto
                .collect(Collectors.toList());
    }

    @Transactional
    public String checkInMember(Long eventId, String personalId, String userEmail) {
        // Validate Input
        validator.validateMemberAccess(userEmail, "check-in to donation events");
        // Fetch Data
        Account member = accountRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = validator.getRegistrationOrThrow(personalId, event);
        validator.validateCheckIn(event, registration, member);

        registration.setStatus(Status.CHECKED_IN);
        eventRegistrationRepository.save(registration);
        return "Checked-in successfully";
    }

    private void recordSingleBloodDonation(SingleBloodUnitRecordDto record, DonationEvent event, Account donor) {
        EventRegistration registration = eventRegistrationRepository.findByEventAndAccount(event, donor)
                .orElseThrow(() -> new RuntimeException(String.format("User %s is not registered for this event", donor.getId())));
        if (registration.getStatus() != Status.CHECKED_IN) {
            throw new RuntimeException(String.format("User %s is not checked in for this event", donor.getId()));
        }

        Profile profile = donor.getProfile();

        BloodUnit bloodUnit = new BloodUnit();
        bloodUnit.setEvent(event);
        bloodUnit.setDonor(donor);
        bloodUnit.setVolume(record.getVolume());
        bloodUnit.setBloodType(profile.getBloodType());
        bloodUnit.setStatus(Status.PENDING);

        if (event.getDonationType().equals(DonationType.WHOLE_BLOOD)) {
            bloodUnit.setComponentType(ComponentType.WHOLE_BLOOD);
            profile.setNextEligibleDonationDate(event.getDonationDate().plusWeeks(12));
        } else {
            bloodUnit.setComponentType(ComponentType.PLATELETS);
            profile.setNextEligibleDonationDate(event.getDonationDate().plusWeeks(3));
        }

        profile.setLastDonationDate(event.getDonationDate());
        profileRepository.save(profile);
        bloodUnitRepository.save(bloodUnit);

        registration.setStatus(Status.COMPLETED);
        eventRegistrationRepository.save(registration);
    }
}

