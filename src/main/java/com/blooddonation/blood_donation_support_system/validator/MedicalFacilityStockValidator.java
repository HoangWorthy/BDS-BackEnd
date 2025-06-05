package com.blooddonation.blood_donation_support_system.validator;

import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.MedicalFacilityStockRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MedicalFacilityStockValidator {
    @Autowired
    private MedicalFacilityStockRepository medicalFacilityStockRepository;
    @Autowired
    private BloodUnitRepository bloodUnitRepository;
    @Autowired
    private UserRepository userRepository;

    public void validateStaffAccess(String userEmail) {
        User staff = userRepository.findByEmail(userEmail);
        if (staff == null) {
            throw new RuntimeException("Staff does not exist");
        }
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff can add blood units to stock");
        }
    }

    public List<BloodUnit> validateAndGetBloodUnits(Long eventId) {
        List<BloodUnit> bloodUnits = bloodUnitRepository.findByEventId(eventId);
        if (bloodUnits == null || bloodUnits.isEmpty()) {
            throw new RuntimeException(String.format("No blood units found for event ID: %d", eventId));
        }
        return bloodUnits;
    }

    public void validateBloodDivisionInput(BloodType bloodType, Double amount) {
        if (bloodType == null || amount == null || amount <= 0) {
            throw new RuntimeException("Invalid blood type or amount");
        }
    }

    public MedicalFacilityStock validateAndGetWholeBloodStock(BloodType bloodType, Double amount) {
        MedicalFacilityStock wholeBloodStock = medicalFacilityStockRepository
                .findByBloodTypeAndComponentType(bloodType, ComponentType.WHOLE_BLOOD)
                .orElseThrow(() -> new RuntimeException(String.format("No whole blood stock found for blood type: %s", bloodType)));

        if (wholeBloodStock.getVolume() < amount) {
            throw new RuntimeException(String.format("Insufficient whole blood stock. Required: %.2f ml, Available: %.2f ml",
                    amount, wholeBloodStock.getVolume()));
        }
        return wholeBloodStock;
    }
}
