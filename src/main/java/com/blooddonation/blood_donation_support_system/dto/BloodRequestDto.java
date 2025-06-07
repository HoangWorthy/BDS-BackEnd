package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.enums.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequestDto {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String personalId;
    private BloodRequestStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime endTime;
    private Urgency urgency;
    private BloodType bloodType;
    private List<ComponentRequestDto> componentRequests;
}
