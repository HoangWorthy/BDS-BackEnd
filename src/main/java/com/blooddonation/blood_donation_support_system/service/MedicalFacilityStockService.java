package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.enums.BloodType;


public interface MedicalFacilityStockService {
    String addBloodUnitsToStockByEventId(Long eventId, String userEmail);
    String divideWholeBloodInStock(BloodType bloodType, Double amount, String userEmail);
}