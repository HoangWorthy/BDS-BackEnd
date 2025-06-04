package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.Gender;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column
    private String email;

    @NotBlank
    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String address;

    @Column
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate lastDonationDate;

    @Column
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate nextEligibleDonationDate;

    @Column
    private String personalId;
}
