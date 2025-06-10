package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.Gender;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private Long accountId;
    private String name;
    private String phone;
    private String address;
    private BloodType bloodType;
    private Gender gender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate lastDonationDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate nextEligibleDonationDate;

    private Status status;

    private String personalId;
}
