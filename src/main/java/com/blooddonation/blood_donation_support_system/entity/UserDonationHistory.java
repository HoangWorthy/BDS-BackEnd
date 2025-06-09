//package com.blooddonation.blood_donation_support_system.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "userDonationHistory")
//@Entity
//public class UserDonationHistory {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id")
//    private Account account;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "event_id")
//    private DonationEvent event;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "registration_id")
//    private EventRegistration registration;
//
//    // Getters and setters
//}
