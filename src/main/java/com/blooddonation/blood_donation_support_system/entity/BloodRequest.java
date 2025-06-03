package com.blooddonation.blood_donation_support_system.entity;

import com.blooddonation.blood_donation_support_system.enums.BloodType;
import com.blooddonation.blood_donation_support_system.enums.ComponentType;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.enums.Urgency;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String address;

    private String personal_id;

    @Enumerated(EnumType.STRING)
    private ComponentType componentType;

    private double volume;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Urgency urgency;

    private BloodType bloodType;
}
