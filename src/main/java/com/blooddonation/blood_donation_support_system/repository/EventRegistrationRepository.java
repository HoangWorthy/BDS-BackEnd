package com.blooddonation.blood_donation_support_system.repository;

import com.blooddonation.blood_donation_support_system.entity.*;
import com.blooddonation.blood_donation_support_system.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventId(Long eventId);
    Optional<EventRegistration> findByEventAndAccount(DonationEvent event, Account account);
    Optional<EventRegistration> findByEventAndProfileId(DonationEvent event, Long profileId);
    Page<EventRegistration> findByEventAndTimeSlot(DonationEvent event, DonationTimeSlot timeSlot, Pageable pageable);
    boolean existsByAccountAndEvent(Account account, DonationEvent event);
    Optional<EventRegistration> findByAccountAndEventAndStatus(Account account, DonationEvent event, Status status);
    Page<EventRegistration> findByAccount(Account account, Pageable pageable);
    Page<EventRegistration> findByEventIdAndStatusNot(Long eventId, Status status, Pageable pageable);
}