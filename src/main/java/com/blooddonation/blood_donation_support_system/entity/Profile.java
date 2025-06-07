package com.blooddonation.blood_donation_support_system.entity;

    import com.blooddonation.blood_donation_support_system.enums.*;
    import com.fasterxml.jackson.annotation.JsonFormat;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Builder
    @Table(name = "profiles")
    public class Profile {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "account_id", nullable = true)
        private Long accountId;

        private String name;

        private String phone;

        private String address;

        @Enumerated(EnumType.STRING)
        private BloodType bloodType;

        @Enumerated(EnumType.STRING)
        private Gender gender;

        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate dateOfBirth;

        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate lastDonationDate;

        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate nextEligibleDonationDate;

        private String personalId;
    }