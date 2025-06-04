package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_registrations")
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private DonationEvent event;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id")
    private DonationTimeSlot timeSlot;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DonationType donationType;


    @PrePersist
    public void prePersist() {
        // Set default registration date if not provided
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
        // Set default status if not provided
        if (status == null) {
            status = Status.PENDING;
        }

        // Update registered member count in donation event
        if (event != null) {
            Integer currentRegisteredCount = event.getRegisteredMemberCount();
            event.setRegisteredMemberCount(currentRegisteredCount + 1);

            if (event.getRegisteredMemberCount() > event.getTotalMemberCount()) {
                throw new RuntimeException("Event is full");
            }
        }
        // Update current registrations in the time slot
        if (timeSlot != null) {
            Integer currentCount = timeSlot.getCurrentRegistrations();
            timeSlot.setCurrentRegistrations(currentCount + 1);

            // Ensure current registrations do not exceed max capacity
            if (timeSlot.getCurrentRegistrations() > timeSlot.getMaxCapacity()) {
                throw new RuntimeException("Time slot is full");
            }
        }
    }

    @PreRemove
    public void preRemove() {
        // Decrease registered member count in donation event
        if (event != null) {
            Integer currentRegisteredCount = event.getRegisteredMemberCount();
            // Ensure count never goes below 0
            event.setRegisteredMemberCount(Math.max(0, currentRegisteredCount - 1));
        }
        if (timeSlot != null) {
            Integer currentCount = timeSlot.getCurrentRegistrations();
            event.setRegisteredMemberCount(Math.max(0, currentCount - 1));
        }
    }
}
