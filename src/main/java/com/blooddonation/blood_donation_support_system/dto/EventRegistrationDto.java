package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRegistrationDto {
    private Long id;
    private Long userId;         // Representing user by ID
    private Long eventId;        // Representing event by ID
    private BloodType bloodType;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;

    private Status status;
}
