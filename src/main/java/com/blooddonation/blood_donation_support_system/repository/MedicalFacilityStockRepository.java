package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.entity.ComponentRequest;
import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MedicalFacilityStockRepository extends JpaRepository<MedicalFacilityStock, Long> {
    Optional<MedicalFacilityStock> findByBloodTypeAndComponentType(BloodType bloodType, ComponentType componentType);
    List<MedicalFacilityStock> findByBloodTypeAndComponentTypeOrderByExpiryDateAsc(BloodType bloodType, ComponentType componentType);
    @Query("SELECT m FROM MedicalFacilityStock m WHERE m.expiryDate >= CURRENT_DATE")
    List<MedicalFacilityStock> findAllAvailableBlood();
    @Query("SELECT m FROM MedicalFacilityStock m WHERE m.bloodType = :bloodType AND m.componentType IN :componentTypes AND m.expiryDate >= CURRENT_DATE")
    List<MedicalFacilityStock> findAvailableBloodByType(BloodType bloodType, List<ComponentType> componentTypes);
}
