package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationEventDto {
    private Long id;
    private String name;
    private String location;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate donationDate;

    private LocalTime startTime;
    private LocalTime endTime;
    private Integer registeredMemberCount;
    private Integer totalMemberCount;
    private Status status;
    private Long userId; // Assuming we only need the ID for the DTO

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdDate;
}
