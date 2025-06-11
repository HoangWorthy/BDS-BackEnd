package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.DonationType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationEventDto {
    private Long id;
    @NotBlank(message = "Event name cannot be blank")
    @Size(min = 5, max = 100, message = "Event name must be between 5 and 100 characters")
    private String name;

    @NotBlank(message = "Location cannot be blank")
    @Size(min = 5, max = 200, message = "Location must be between 5 and 200 characters")
    private String location;

    private Integer registeredMemberCount;

    @NotNull(message = "Total member count is required")
    @Min(value = 1, message = "Total member count must be at least 1")
    private Integer totalMemberCount;
    private Status status;
    @NotNull(message = "Donation type is required")
    private DonationType donationType;
    private Long accountId;
    @Valid
    @NotEmpty(message = "At least one time slot is required")
    private List<DonationTimeSlotDto> timeSlotDtos;
    private LocalDate createdDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Donation date is required")
    @FutureOrPresent(message = "Donation date must be in the future or present")
    private LocalDate donationDate;



}
