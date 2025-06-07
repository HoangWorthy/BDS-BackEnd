package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {
    List<BloodUnit> findByEventId(Long eventId);
}
