package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleBloodUnitRecordDto {

    private Long profileId;
    @NotNull(message = "Blood volume is required")
    private Double volume;
}
