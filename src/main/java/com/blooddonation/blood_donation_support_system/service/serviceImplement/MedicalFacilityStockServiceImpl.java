package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.MedicalFacilityStockRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
import com.blooddonation.blood_donation_support_system.validator.MedicalFacilityStockValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicalFacilityStockServiceImpl implements MedicalFacilityStockService {
    @Autowired
    private MedicalFacilityStockRepository medicalFacilityStockRepository;
    @Autowired
    private BloodUnitRepository bloodUnitRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MedicalFacilityStockValidator validator;

    @Transactional
    public String addBloodUnitsToStockByEventId(Long eventId, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail);

        // Fetch Data
        List<BloodUnit> bloodUnits = validator.validateAndGetBloodUnits(eventId);

        // Add Blood Units to Stock
        for (BloodUnit bloodUnit : bloodUnits) {
            MedicalFacilityStock stock = new MedicalFacilityStock();
            stock.setBloodType(bloodUnit.getBloodType());
            stock.setVolume(bloodUnit.getVolume());
            stock.setComponentType(bloodUnit.getComponentType());
            updateOrCreateStock(stock);

            // Update Blood Unit status to COMPLETED
            bloodUnit.setStatus(Status.COMPLETED);
            bloodUnitRepository.save(bloodUnit);
        }
        return String.format("Successfully added %d blood units to stock", bloodUnits.size());
    }

    @Transactional
    public String divideWholeBloodInStock(BloodType bloodType, Double amount, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail);
        validator.validateBloodDivisionInput(bloodType, amount);

        // Fetch Data
        MedicalFacilityStock wholeBloodStock = validator.validateAndGetWholeBloodStock(bloodType, amount);

        MedicalFacilityStock tempStock = new MedicalFacilityStock();
        tempStock.setBloodType(bloodType);
        tempStock.setComponentType(ComponentType.WHOLE_BLOOD);
        tempStock.setVolume(amount);

        List<MedicalFacilityStock> components = divideWholeBloodIntoComponents(tempStock);
        for (MedicalFacilityStock component : components) {
            updateOrCreateStock(component);
        }

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
