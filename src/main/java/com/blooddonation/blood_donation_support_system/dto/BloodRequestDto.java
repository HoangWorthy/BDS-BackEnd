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
    private boolean isPregnant;
    private boolean isDisabled;
    private boolean haveServed;
    private boolean isAutomation;

    public int calculatePriority() {
        int priority = 0;
        if (urgency != null) {
            switch (urgency) {
                case HIGH: priority += 3; break;
                case MEDIUM: priority += 2; break;
                case LOW: priority += 1; break;
            }
        }
        if (isPregnant) priority += 2;
        if (isDisabled) priority += 1;
        if (haveServed) priority += 1;
        return priority;
    }
}
