package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventRequestDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.DonationEventRequest;
import com.blooddonation.blood_donation_support_system.entity.DonationTimeSlot;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventMapper;
import com.blooddonation.blood_donation_support_system.mapper.DonationEventRequestMapper;
import com.blooddonation.blood_donation_support_system.repository.*;
import com.blooddonation.blood_donation_support_system.service.DonationEventRequestService;
import com.blooddonation.blood_donation_support_system.service.DonationTimeSlotService;
import com.blooddonation.blood_donation_support_system.validator.DonationEventValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationEventRequestServiceImpl implements DonationEventRequestService {
    @Autowired
    private DonationEventRepository donationEventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Autowired
    private DonationTimeSlotService donationTimeSlotService;

    @Autowired
    private DonationEventValidator validator;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private DonationEventRequestRepository donationEventRequestRepository;

    @Transactional
    public String createDonationRequest(DonationEventDto donationEventDto, String staffEmail) {
        Account staff = accountRepository.findByEmail(staffEmail);

        DonationEventRequest donationEventRequest = DonationEventRequestMapper.createDonation(donationEventDto, staff);
        donationEventRequestRepository.save(donationEventRequest);
        return "Donation request created successfully";
    }

    @Transactional
    public String verifyDonation(Long requestId, String action) {
        DonationEventRequest request = validator.getRequestOrThrow(requestId);
        validator.validateEventVerification(action);

        if (action.equals("approve")) {
            createDonation(request.getNewDonationEventDto(), request.getAccount().getEmail());
            request.setStatus(Status.APPROVED);
            donationEventRequestRepository.save(request);
            return "Donation request approved";
        } else {
            request.setStatus(Status.REJECTED);
            donationEventRequestRepository.save(request);
            return "Donation request rejected";
        }
    }

    public Page<DonationEventRequestDto> getSortedPaginatedRequests(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return donationEventRequestRepository.findAll(pageable).map(DonationEventRequestMapper::toDto);
    }

    public Page<DonationEventRequestDto> getSortedPaginatedRequestsByAccount(String email, int pageNumber, int pageSize, String sortBy, boolean ascending) {
        Account account = accountRepository.findByEmail(email);
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return donationEventRequestRepository.findByAccount(account, pageable).map(DonationEventRequestMapper::toDto);
    }

    public DonationEventRequestDto getDonationRequestById(Long requestId) {
        DonationEventRequest donationEventRequest = validator.getRequestOrThrow(requestId);
        return DonationEventRequestMapper.toDto(donationEventRequest);
    }

    public String createDonation(DonationEventDto donationEventDto, String staffEmail) {
        // Fetch Data
        Account staff = accountRepository.findByEmail(staffEmail);

        // Create And Save Donation Event
        DonationEvent donationEvent = DonationEventMapper.createDonation(donationEventDto, staff);
        DonationEvent savedDonationEvent = donationEventRepository.save(donationEvent);

        // Create time slots for the event
        List<DonationTimeSlot> timeSlots = donationTimeSlotService.createTimeSlotsForEvent(donationEventDto.getTimeSlotDtos(), savedDonationEvent);
        savedDonationEvent.setTimeSlots(timeSlots);
        return "Donation event created successfully";
    }
}
