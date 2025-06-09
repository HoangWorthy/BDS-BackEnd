package com.blooddonation.blood_donation_support_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDonationHistoryDto {
    private Long id;
    private Long accountId;
    private Long eventId;
    private Long registrationId;
}
