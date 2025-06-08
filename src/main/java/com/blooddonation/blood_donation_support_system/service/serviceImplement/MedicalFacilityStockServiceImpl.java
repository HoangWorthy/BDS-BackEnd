package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.dto.MedicalFacilityStockDto;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.ComponentRequest;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.MedicalFacilityStockMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.MedicalFacilityStockRepository;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
import com.blooddonation.blood_donation_support_system.validator.MedicalFacilityStockValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
            if (bloodUnit.getComponentType() == ComponentType.WHOLE_BLOOD) {
                // Create temporary stock for division
                MedicalFacilityStock wholeBloodStock = new MedicalFacilityStock();
                wholeBloodStock.setBloodType(bloodUnit.getBloodType());
                wholeBloodStock.setComponentType(ComponentType.WHOLE_BLOOD);
                wholeBloodStock.setVolume(bloodUnit.getVolume());

                // Divide and add components
                List<MedicalFacilityStock> components = divideWholeBloodIntoComponents(wholeBloodStock, eventId);
                for (MedicalFacilityStock component : components) {
                    updateOrCreateStock(component);
                }
            } else {
                // For non-whole blood units, add directly
                MedicalFacilityStock stock = new MedicalFacilityStock();
                stock.setBloodType(bloodUnit.getBloodType());
                stock.setVolume(bloodUnit.getVolume());
                stock.setComponentType(bloodUnit.getComponentType());
                updateOrCreateStock(stock);
            }

            // Update Blood Unit status to COMPLETED
            bloodUnit.setStatus(Status.COMPLETED);
            bloodUnitRepository.save(bloodUnit);
        }
        return String.format("Successfully added %d blood units to stock", bloodUnits.size());
    }

    @Transactional
    public List<MedicalFacilityStock> withdrawBloodFromStock(BloodType bloodType, ComponentType componentType, double requestedVolume, String userEmail) {
        // Validate Input
        validator.validateStaffAccess(userEmail);

        List<MedicalFacilityStock> stocksToWithdraw = new ArrayList<>();
        double remainingVolume = requestedVolume;

        // Get all matching stocks ordered by expiry date ascending
        List<MedicalFacilityStock> availableStocks = medicalFacilityStockRepository
            .findByBloodTypeAndComponentTypeOrderByExpiryDateAsc(bloodType, componentType);

        if (availableStocks.isEmpty()) {
            throw new RuntimeException("No matching blood stock available");
        }

        // Calculate total available volume
        double totalAvailableVolume = availableStocks.stream()
            .mapToDouble(MedicalFacilityStock::getVolume)
            .sum();

        if (totalAvailableVolume < requestedVolume) {
            throw new RuntimeException("Insufficient blood stock available only has " + totalAvailableVolume + " available");
        }

        // Withdraw from stocks starting with earliest expiry date
        for (MedicalFacilityStock stock : availableStocks) {
            if (remainingVolume <= 0) break;

            if (stock.getVolume() <= remainingVolume) {
                // Use entire stock
                remainingVolume -= stock.getVolume();
                stocksToWithdraw.add(stock);
                medicalFacilityStockRepository.delete(stock);
            } else {
                // Use partial stock
                MedicalFacilityStock updatedStock = new MedicalFacilityStock();
                updatedStock.setId(stock.getId());
                updatedStock.setBloodType(stock.getBloodType());
                updatedStock.setComponentType(stock.getComponentType());
                updatedStock.setVolume(stock.getVolume() - remainingVolume);
                updatedStock.setExpiryDate(stock.getExpiryDate());

                MedicalFacilityStock withdrawnStock = new MedicalFacilityStock();
                withdrawnStock.setBloodType(stock.getBloodType());
                withdrawnStock.setComponentType(stock.getComponentType());
                withdrawnStock.setVolume(remainingVolume);
                withdrawnStock.setExpiryDate(stock.getExpiryDate());

                medicalFacilityStockRepository.save(updatedStock);
                stocksToWithdraw.add(withdrawnStock);
                remainingVolume = 0;
            }
        }

        return stocksToWithdraw;
    }

    public String updateBeforeWithdraw(String userEmail) {
        List<MedicalFacilityStock> stocks = medicalFacilityStockRepository.findAll();
        StringBuilder removedStock = new StringBuilder();
        int count = 0;

        for (MedicalFacilityStock stock : stocks) {
            if (stock.getExpiryDate().isBefore(LocalDate.now())) {
                removedStock.append("Please removed expired stock at your facility: ID=")
                        .append(stock.getId())
                        .append(", Type=")
                        .append(stock.getBloodType())
                        .append(", Component=")
                        .append(stock.getComponentType())
                        .append(", Volume=")
                        .append(stock.getVolume())
                        .append(", Expiry=")
                        .append(stock.getExpiryDate())
                        .append("\n");

                medicalFacilityStockRepository.delete(stock);
                count++;
            }
        }

        if (count == 0) {
            return "No expired stocks found";
        }

        return String.format("Removed %d expired stocks:\n%s", count, removedStock.toString());
    }

    @Override
    public List<MedicalFacilityStockDto> getAllAvailableBlood() {
        List<MedicalFacilityStock> medicalFacilityStocks = medicalFacilityStockRepository.findAllAvailableBlood();
        return medicalFacilityStocks.stream()
                .map(MedicalFacilityStockMapper::toDto)
                .toList();
    }

    @Override
    public List<MedicalFacilityStockDto> getAvailableBloodByType(BloodType bloodType, List<ComponentType> componentTypes) {
        List<MedicalFacilityStock> medicalFacilityStocks = medicalFacilityStockRepository.findAvailableBloodByType(bloodType, componentTypes);
        return medicalFacilityStocks.stream()
                .map(MedicalFacilityStockMapper::toDto)
                .toList();
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
    protected List<MedicalFacilityStock> divideWholeBloodIntoComponents(MedicalFacilityStock wholeBloodStock, Long eventId) {

        List<MedicalFacilityStock> components = new ArrayList<>();
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        double originalVolume = wholeBloodStock.getVolume();

        // Create Plasma component (55%)
        MedicalFacilityStock plasma = new MedicalFacilityStock();
        plasma.setBloodType(wholeBloodStock.getBloodType());
        plasma.setComponentType(ComponentType.PLASMA);
        plasma.setVolume(originalVolume * 0.55);
        plasma.setExpiryDate(donationEvent.getDonationDate().plusYears(1));
        components.add(plasma);

        // Create RBC component (44%)
        MedicalFacilityStock rbc = new MedicalFacilityStock();
        rbc.setBloodType(wholeBloodStock.getBloodType());
        rbc.setComponentType(ComponentType.RED_BLOOD_CELLS);
        rbc.setVolume(originalVolume * 0.44);
        rbc.setExpiryDate(donationEvent.getDonationDate().plusDays(42));
        components.add(rbc);

        // Create Platelet component (1%)
        MedicalFacilityStock platelet = new MedicalFacilityStock();
        platelet.setBloodType(wholeBloodStock.getBloodType());
        platelet.setComponentType(ComponentType.PLATELETS);
        platelet.setVolume(originalVolume * 0.01);
        platelet.setExpiryDate(donationEvent.getDonationDate().plusWeeks(1));
        components.add(platelet);

        return components;
    }
}
