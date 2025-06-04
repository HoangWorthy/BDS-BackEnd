package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationEventDto {
    private Long id;
    private String name;
    private String location;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate donationDate;

    private Integer registeredMemberCount;
    private Integer totalMemberCount;
    private Status status;
    private DonationType donationType;
    private Long userId; // Assuming we only need the ID for the DTO


    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate createdDate;

//    private List<DonationEventDto> donationEvents;
    private List<DonationTimeSlotDto> timeSlotDtos;

}
