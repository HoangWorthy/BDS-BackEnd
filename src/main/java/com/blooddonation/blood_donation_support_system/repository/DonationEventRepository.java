package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.DonationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationEventRepository extends JpaRepository<DonationEvent, Long> {
    List<DonationEvent> findByDonationDateBetween(LocalDate startDate, LocalDate endDate);
}
