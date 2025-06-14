package com.blooddonation.blood_donation_support_system.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileWithFormResponseDto {
    private ProfileDto profile;
    @JsonRawValue
    private String jsonForm;
}
