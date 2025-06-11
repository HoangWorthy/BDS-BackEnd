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
    DonationEventDto getDonationEventById(Long eventId);
    List<DonationEventDto> getAllDonationEvents();
    List<DonationEventDto> getEventByBetweenDates(LocalDate startDate, LocalDate endDate);
    String recordMultipleBloodDonations(Long eventId, List<SingleBloodUnitRecordDto> records, String userEmail);
    List<AccountDto> getEventDonors(Long eventId, Long timeSlotId);
    List<ProfileDto> getEventDonorProfiles(Long eventId);
}