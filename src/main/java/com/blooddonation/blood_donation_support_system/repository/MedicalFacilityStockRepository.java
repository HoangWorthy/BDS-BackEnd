package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.MedicalFacilityStock;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MedicalFacilityStockRepository extends JpaRepository<MedicalFacilityStock, Long> {
    Optional<MedicalFacilityStock> findByBloodTypeAndComponentType(BloodType bloodType, ComponentType componentType);
    List<MedicalFacilityStock> findByBloodTypeAndComponentTypeOrderByExpiryDateAsc(BloodType bloodType, ComponentType componentType);

}
