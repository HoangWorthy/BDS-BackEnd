package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEvent(DonationEvent event);
    Optional<EventRegistration> findByEventAndAccount(DonationEvent event, Account account);
    Optional<EventRegistration> findByEventAndProfileId(DonationEvent event, Long profileId);
    List<EventRegistration> findByEventAndTimeSlot(DonationEvent event, DonationTimeSlot timeSlot);
    boolean existsByAccountAndEvent(Account account, DonationEvent event);
    Optional<EventRegistration> findByAccountAndEventAndStatus(Account account, DonationEvent event, Status status);
    List<EventRegistration> findByAccount(Account account);
}