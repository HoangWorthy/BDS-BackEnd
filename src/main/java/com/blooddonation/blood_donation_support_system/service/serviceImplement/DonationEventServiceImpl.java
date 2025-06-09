package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.*;
import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.*;
import com.blooddonation.blood_donation_support_system.repository.*;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
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
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private CheckinTokenRepository checkinTokenRepository;
    @Autowired
    private CheckinTokenService checkinTokenService;
    @Autowired
    private CheckinTokenMapper checkinTokenMapper;
    @Autowired
    private DonationEventMapper donationEventMapper;

    @Transactional
    public String createDonation(DonationEventDto donationEventDto, String staffEmail) {
        // Validate Input
        validator.validateStaffAccess(staffEmail, "create donation events");
        validator.validateEventCreation(donationEventDto);

        // Fetch Data
        Account staff = accountRepository.findByEmail(staffEmail);

        // Create And Save Donation Event
        DonationEvent donationEvent = DonationEventMapper.toEntity(donationEventDto, staff);
//        DonationEventMapper.toEntity(donationEventDto, staff);
        DonationEvent savedDonationEvent = donationEventRepository.save(donationEvent);

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
        EventRegistrationDto eventRegistrationDto = new EventRegistrationDto();

//        EventRegistration registration = new EventRegistration();
//        registration.setAccount(account);
//        registration.setEvent(donationEvent);
//        registration.setTimeSlot(timeSlot);
//        registration.setBloodType(profile.getBloodType());
//        registration.setDonationType(donationEvent.getDonationType());
//        registration.setStatus(Status.PENDING);
        EventRegistration registration = EventRegistrationMapper.toEntity(eventRegistrationDto,account,donationEvent,timeSlot,profile);
        eventRegistrationRepository.save(registration);

        // Generate CheckinToken
        CheckinTokenDto tokenDto = checkinTokenService.generateTokenForProfile(profile, donationEvent);

        // Generate QR code URL and image
        String qrUrl = String.format("http://localhost:8080/donation-events/check-in/info/%d?checkinToken=%s", eventId, tokenDto.getToken());
        try {
            byte[] qrCode = qrCodeService.generateQRCode(qrUrl);
            registration.setQrCode(qrCode);
            CheckinToken checkinToken = CheckinTokenMapper.toEntity(tokenDto);
            registration.setCheckinToken(checkinToken);
            eventRegistrationRepository.save(registration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
        }
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
    public ProfileDto registerForGuest(Long eventId, ProfileDto profileDto, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail, "register for donation events");

        // Fetch Data
        Account staff = accountRepository.findByEmail(userEmail);
        DonationEvent event = validator.getEventOrThrow(eventId);

        // Check if profile with personalId already exists
        Optional<Profile> existingProfile = profileRepository.findByPersonalId(profileDto.getPersonalId());
        if (existingProfile.isPresent()) {
            throw new RuntimeException("Profile with this Personal ID already exists");
        }

        // Create and save profile
//        Profile profile = new Profile();
//        profile.setName(profileDto.getName());
//        profile.setDateOfBirth(profileDto.getDateOfBirth());
//        profile.setGender(profileDto.getGender());
//        profile.setAddress(profileDto.getAddress());
//        profile.setPhone(profileDto.getPhone());
//        profile.setPersonalId(profileDto.getPersonalId());
//        profile.setBloodType(profileDto.getBloodType());
//        profile.setLastDonationDate(profileDto.getLastDonationDate());
        Profile profile = ProfileMapper.toEntity(profileDto);
        Profile savedProfile = profileRepository.save(profile);

        // Create event registration for guest
//        EventRegistration registration = new EventRegistration();
//        registration.setEvent(event);
//        registration.setAccount(staff);
//        registration.setBloodType(profile.getBloodType());
//        registration.setDonationType(event.getDonationType());
//        registration.setStatus(Status.CHECKED_IN);
        EventRegistration registration = EventRegistrationMapper.registerOfflineEntity(event, staff, profile);
        eventRegistrationRepository.save(registration);

        return ProfileMapper.toDto(savedProfile);
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
    public byte[] getQRCodeForUser(Long eventId, String email) {
        // Validate Input
        validator.validateMemberAccess(email, "get QR code for user");

        // Fetch Data
        Account member = accountRepository.findByEmail(email);
        Account member1 = validator.validateAndGetMemberAccount(member.getProfile().getPersonalId());
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = validator.validateAndGetExistingRegistration(member1, event);
        return validator.validateQRCode(registration.getQrCode());
    }


    @Transactional
    public String checkInMember(Long eventId, String action, String userEmail, ProfileDto profileDto) {
        // Validate Input
        validator.validateStaffAccess(userEmail, "check-in to donation events");
        validator.validateCheckinVerification(action);
        // Fetch Data
        DonationEvent event = validator.getEventOrThrow(eventId);
        EventRegistration registration = validator.getRegistrationOrThrow(profileDto.getPersonalId(), event);

        if (action.equals("approve")) {
        registration.setStatus(Status.CHECKED_IN);
        eventRegistrationRepository.save(registration);
        } else if (action.equals("reject")) {
            registration.setStatus(Status.REJECTED);
        }
        return "Checked-in " + action + " successfully";
    }

    private void recordSingleBloodDonation(SingleBloodUnitRecordDto record, DonationEvent event, Account donor) {
        EventRegistration registration = eventRegistrationRepository.findByEventAndAccount(event, donor)
                .orElseThrow(() -> new RuntimeException(String.format("User %s is not registered for this event", donor.getId())));
        if (registration.getStatus() != Status.CHECKED_IN) {
            throw new RuntimeException(String.format("User %s is not checked in for this event", donor.getId()));
        }

        Profile profile = donor.getProfile();
        // Use the mapper to create the BloodUnit
        BloodUnit bloodUnit = BloodUnitMapper.toEntity1(record, donor, event, profile);
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