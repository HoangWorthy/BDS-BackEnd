package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;

import java.time.LocalDate;
import java.util.List;

public interface DonationEventService {
    String createDonation(DonationEventDto donationEventDto, String staffEmail);
    String verifyDonationEvent(Long eventId, String adminEmail, String action);
    String registerForEventOnline(Long eventId, Long timeSlotId, String userEmail);
    String registerForEventOffline(Long eventId, String personalId, String userEmail);
    ProfileDto registerForGuest(Long eventId, ProfileDto profileDto, String userEmail);
    DonationEventDto getDonationEventById(Long eventId);
    List<DonationEventDto> getAllDonationEvents();
    List<DonationEventDto> getEventByBetweenDates(LocalDate startDate, LocalDate endDate);
    String recordMultipleBloodDonations(Long eventId, List<SingleBloodUnitRecordDto> records, String userEmail);
    List<AccountDto> getEventDonors(Long eventId, Long timeSlotId, String userEmail);
    String checkInMember(Long eventId, String personalId, String userEmail);
    String cancelEventRegistration(Long eventId, String userEmail);
}