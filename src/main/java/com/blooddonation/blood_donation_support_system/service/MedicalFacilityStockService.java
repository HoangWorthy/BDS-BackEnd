package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;

import java.util.List;


public interface MedicalFacilityStockService {
    String addBloodUnitsToStockByEventId(Long eventId, String userEmail);
    List<MedicalFacilityStock> withdrawBloodFromStock(BloodType bloodType, ComponentType componentType, double requestedVolume, String userEmail);
    String updateBeforeWithdraw(String userEmail);
    List<MedicalFacilityStockDto> getAllAvailableBlood();
    List<MedicalFacilityStockDto> getAvailableBloodByType(BloodType bloodType, List<ComponentType> componentTypes);
}