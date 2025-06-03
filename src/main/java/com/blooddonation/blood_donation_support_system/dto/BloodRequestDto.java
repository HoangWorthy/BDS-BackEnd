package com.blooddonation.blood_donation_support_system.dto;

import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.enums.Urgency;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BloodRequestDto {
    private Long id;

    private String name;

    private String phone;

    private String address;

    private String personal_id;

    private ComponentType componentType;

    private double volume;

    private Status status;

    private LocalDateTime createdTime;

    private LocalDateTime endTime;

    private Urgency urgency;

    private BloodType bloodType;

}
