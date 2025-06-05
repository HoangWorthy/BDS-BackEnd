package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.MedicalFacilityStockRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicalFacilityStockService {
    @Autowired
    private MedicalFacilityStockRepository medicalFacilityStockRepository;
    @Autowired
    private BloodUnitRepository bloodUnitRepository;
    @Autowired
    private UserRepository userRepository;



    @Transactional
    public String addBloodUnitsToStockByEventId(Long eventId, String userEmail) {
        User staff = userRepository.findByEmail(userEmail);
        if (staff == null) {
            throw new RuntimeException("Staff does not exist");
        }
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff can add blood units to stock");
        }
        List<BloodUnit> bloodUnits = bloodUnitRepository.findByEventId(eventId);
        if (bloodUnits == null || bloodUnits.isEmpty()) {
            return "No blood units found for the given event ID.";
        }

        for (BloodUnit bloodUnit : bloodUnits) {
                MedicalFacilityStock stock = new MedicalFacilityStock();
                stock.setBloodType(bloodUnit.getBloodType());
                stock.setVolume(bloodUnit.getVolume());
                stock.setComponentType(bloodUnit.getComponentType());
                updateOrCreateStock(stock);
        }
        return "Blood units added to stock successfully.";
    }

    @Transactional
    public String divideWholeBloodInStock(BloodType bloodType, Double amount, String userEmail) {
        User staff = userRepository.findByEmail(userEmail);
        if (staff == null) {
            throw new RuntimeException("Staff does not exist");
        }
        if (!staff.getRole().equals(Role.STAFF)) {
            throw new RuntimeException("Only staff can divide whole blood into components");
        }
        if (bloodType == null || amount == null || amount <= 0) {
            return "Invalid blood type or amount.";
        }

        MedicalFacilityStock wholeBloodStock = medicalFacilityStockRepository
                .findByBloodTypeAndComponentType(bloodType, ComponentType.WHOLE_BLOOD)
                .orElseThrow(() -> new RuntimeException("No whole blood stock found for the given blood type."));

        if (wholeBloodStock.getVolume() < amount) {
            return "Insufficient whole blood stock to divide.";
        }

        // Create a temporary stock object with the requested amount
        MedicalFacilityStock tempStock = new MedicalFacilityStock();
        tempStock.setBloodType(bloodType);
        tempStock.setComponentType(ComponentType.WHOLE_BLOOD);
        tempStock.setVolume(amount);

        // Divide into components and update stock
        List<MedicalFacilityStock> components = divideWholeBloodIntoComponents(tempStock);
        for (MedicalFacilityStock component : components) {
            updateOrCreateStock(component);
        }

        // Reduce whole blood stock
        wholeBloodStock.setVolume(wholeBloodStock.getVolume() - amount);
        medicalFacilityStockRepository.save(wholeBloodStock);

        return String.format("Successfully divided %.2f ml of %s whole blood into components", amount, bloodType);
    }

    private void updateOrCreateStock(MedicalFacilityStock newStock) {
        medicalFacilityStockRepository.findByBloodTypeAndComponentType(
                newStock.getBloodType(),
                newStock.getComponentType()
        ).ifPresentOrElse(
                existingStock -> {
                    existingStock.setVolume(existingStock.getVolume() + newStock.getVolume());
                    medicalFacilityStockRepository.save(existingStock);
                },
                () -> medicalFacilityStockRepository.save(newStock)
        );
    }

    @Transactional
    protected List<MedicalFacilityStock> divideWholeBloodIntoComponents(MedicalFacilityStock wholeBloodStock) {
        List<MedicalFacilityStock> components = new ArrayList<>();

        double originalVolume = wholeBloodStock.getVolume();

        // Create Plasma component (55%)
        MedicalFacilityStock plasma = new MedicalFacilityStock();
        plasma.setBloodType(wholeBloodStock.getBloodType());
        plasma.setComponentType(ComponentType.PLASMA);
        plasma.setVolume(originalVolume * 0.55);
        components.add(plasma);

        // Create RBC component (44%)
        MedicalFacilityStock rbc = new MedicalFacilityStock();
        rbc.setBloodType(wholeBloodStock.getBloodType());
        rbc.setComponentType(ComponentType.RED_BLOOD_CELLS);
        rbc.setVolume(originalVolume * 0.44);
        components.add(rbc);

        // Create Platelet component (1%)
        MedicalFacilityStock platelet = new MedicalFacilityStock();
        platelet.setBloodType(wholeBloodStock.getBloodType());
        platelet.setComponentType(ComponentType.PLATELETS);
        platelet.setVolume(originalVolume * 0.01);
        components.add(platelet);

        return components;
    }
}

