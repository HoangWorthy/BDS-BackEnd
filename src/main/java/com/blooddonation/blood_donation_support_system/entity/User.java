package com.blooddonation.blood_donation_support_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
    private int phone;

    @Column
    private String address;

    @Column
    private BloodType bloodType;

    @Column
    private Gender gender;

    @Column
    private Date dateOfBirth;

    @Column
    private Role role;

    @Column
    private Date lastDonationDate;

    @Column
    private int personalId;
}
