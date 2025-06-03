package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;

public class BloodRequestMapper {
    public static BloodRequestDto toBloodRequestDto(BloodRequest bloodRequest) {
        return BloodRequestDto.builder()
                .id(bloodRequest.getId())
                .bloodType(bloodRequest.getBloodType())
                .name(bloodRequest.getName())
                .phone(bloodRequest.getPhone())
                .personal_id(bloodRequest.getPersonal_id())
                .address(bloodRequest.getAddress())
                .componentType(bloodRequest.getComponentType())
                .endTime(bloodRequest.getEndTime())
                .createdTime(bloodRequest.getCreatedTime())
                .volume(bloodRequest.getVolume())
                .status(bloodRequest.getStatus())
                .urgency(bloodRequest.getUrgency())
                .build();
    }
    public static BloodRequest toBloodRequestEntity(BloodRequestDto bloodRequestDto) {
        return BloodRequest.builder()
                .id(bloodRequestDto.getId())
                .bloodType(bloodRequestDto.getBloodType())
                .status(bloodRequestDto.getStatus())
                .endTime(bloodRequestDto.getEndTime())
                .componentType(bloodRequestDto.getComponentType())
                .name(bloodRequestDto.getName())
                .address(bloodRequestDto.getAddress())
                .personal_id(bloodRequestDto.getPersonal_id())
                .phone(bloodRequestDto.getPhone())
                .createdTime(bloodRequestDto.getCreatedTime())
                .volume(bloodRequestDto.getVolume())
                .urgency(bloodRequestDto.getUrgency())
                .build();
    }
}
