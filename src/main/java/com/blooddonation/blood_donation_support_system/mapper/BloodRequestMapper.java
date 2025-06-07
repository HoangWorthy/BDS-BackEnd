package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.entity.ComponentRequest;

import java.util.List;
import java.util.stream.Collectors;

public class BloodRequestMapper {

    public static BloodRequestDto toBloodRequestDto(BloodRequest bloodRequest) {
        return BloodRequestDto.builder()
                .id(bloodRequest.getId())
                .bloodType(bloodRequest.getBloodType())
                .name(bloodRequest.getName())
                .phone(bloodRequest.getPhone())
                .personalId(bloodRequest.getPersonalId())
                .address(bloodRequest.getAddress())
                .endTime(bloodRequest.getEndTime())
                .createdTime(bloodRequest.getCreatedTime())
                .status(bloodRequest.getStatus())
                .urgency(bloodRequest.getUrgency())
                .componentRequests(
                        bloodRequest.getComponentRequests() != null ?
                                bloodRequest.getComponentRequests().stream()
                                        .map(ComponentRequestMapper::toDto)
                                        .collect(Collectors.toList()) : null
                )
                .build();
    }

    public static BloodRequest toBloodRequestEntity(BloodRequestDto dto) {
        BloodRequest bloodRequest = BloodRequest.builder()
                .id(dto.getId())
                .bloodType(dto.getBloodType())
                .status(dto.getStatus())
                .endTime(dto.getEndTime())
                .name(dto.getName())
                .address(dto.getAddress())
                .personalId(dto.getPersonalId())
                .phone(dto.getPhone())
                .createdTime(dto.getCreatedTime())
                .urgency(dto.getUrgency())
                .build();

        if (dto.getComponentRequests() != null) {
            List<ComponentRequest> componentRequests = dto.getComponentRequests().stream()
                    .map((ComponentRequestDto dto1) -> ComponentRequestMapper.toEntity(dto1, bloodRequest))
                    .collect(Collectors.toList());

            bloodRequest.setComponentRequests(componentRequests);
        }

        return bloodRequest;
    }
}
