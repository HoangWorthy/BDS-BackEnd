package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.MedicalFacilityStockMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodUnitRepository;
import com.blooddonation.blood_donation_support_system.repository.MedicalFacilityStockRepository;
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
    private MedicalFacilityStockValidator validator;

    @Transactional
    public String addBloodUnitsToStockByEventId(Long eventId, String userEmail) {
        // Fetch Data
        List<BloodUnit> bloodUnits = validator.validateAndGetBloodUnits(eventId);

        // Add Blood Units to Stock
        for (BloodUnit bloodUnit : bloodUnits) {
            MedicalFacilityStock stock = MedicalFacilityStockMapper.fromBloodUnit(bloodUnit);

            if (bloodUnit.getComponentType() == ComponentType.WHOLE_BLOOD) {
                List<MedicalFacilityStock> components = divideWholeBloodIntoComponents(stock, eventId);
                for (MedicalFacilityStock component : components) {
                    updateOrCreateStock(component);
                }
            } else {
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
        List<MedicalFacilityStock> stocksToWithdraw = new ArrayList<>();
        double remainingVolume = requestedVolume;

        // Fetch stock
        List<MedicalFacilityStock> availableStocks = medicalFacilityStockRepository
                .findByBloodTypeAndComponentTypeOrderByExpiryDateAsc(bloodType, componentType);

        if (availableStocks.isEmpty()) {
            throw new RuntimeException("No matching blood stock available");
        }

        double totalAvailableVolume = availableStocks.stream()
                .mapToDouble(MedicalFacilityStock::getVolume)
                .sum();

        if (totalAvailableVolume < requestedVolume) {
            throw new RuntimeException("Insufficient blood stock: only " + totalAvailableVolume + " available");
        }

        for (MedicalFacilityStock stock : availableStocks) {
            if (remainingVolume <= 0) break;

            if (stock.getVolume() <= remainingVolume) {
                remainingVolume -= stock.getVolume();
                stocksToWithdraw.add(stock);
                medicalFacilityStockRepository.delete(stock);
            } else {
                MedicalFacilityStock updatedStock = MedicalFacilityStockMapper.copyWithNewVolume(stock, stock.getVolume() - remainingVolume);
                MedicalFacilityStock withdrawnStock = MedicalFacilityStockMapper.createWithdrawnStock(stock, remainingVolume);

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
        DonationEvent donationEvent = validator.getEventOrThrow(eventId);
        LocalDate donationDate = donationEvent.getDonationDate();
        double originalVolume = wholeBloodStock.getVolume();
        BloodType bloodType = wholeBloodStock.getBloodType();

        List<MedicalFacilityStock> components = List.of(
                MedicalFacilityStockMapper.createComponent(
                        bloodType,
                        ComponentType.PLASMA,
                        originalVolume * 0.55,
                        donationDate.plusYears(1)
                ),
                MedicalFacilityStockMapper.createComponent(
                        bloodType,
                        ComponentType.RED_BLOOD_CELLS,
                        originalVolume * 0.44,
                        donationDate.plusDays(42)
                ),
                MedicalFacilityStockMapper.createComponent(
                        bloodType,
                        ComponentType.PLATELETS,
                        originalVolume * 0.01,
                        donationDate.plusWeeks(1)
                )
        );

        return components;
    }



}
