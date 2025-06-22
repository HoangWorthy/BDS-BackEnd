package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.Status;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileWithFormResponseDto {
    private ProfileDto profile;
    private String jsonForm;
    private Status status;
}
