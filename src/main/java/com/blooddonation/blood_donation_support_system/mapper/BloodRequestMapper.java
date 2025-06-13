package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.dto.ComponentRequestDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.entity.BloodUnit;
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
                .isDisabled(bloodRequest.isDisabled())
                .haveServed(bloodRequest.isHaveServed())
                .isPregnant(bloodRequest.isPregnant())
                .isAutomation(bloodRequest.isAutomation())
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
                .isPregnant(dto.isPregnant())
                .isDisabled(dto.isDisabled())
                .haveServed(dto.isHaveServed())
                .isAutomation(dto.isAutomation())
                .build();

        if (dto.getComponentRequests() != null) {
            List<ComponentRequest> componentRequests = dto.getComponentRequests().stream()
                    .map((ComponentRequestDto dto1) -> ComponentRequestMapper.toEntity(dto1, bloodRequest))
                    .collect(Collectors.toList());

            bloodRequest.setComponentRequests(componentRequests);
        }

        if(dto.getBloodUnits() != null) {
            List<BloodUnit> bloodUnits = dto.getBloodUnits().stream()
                    .map(dto1 -> BloodUnitMapper.toEntity(dto1, bloodRequest))
                    .collect(Collectors.toList());
            bloodRequest.setBloodUnits(bloodUnits);
        }

        return bloodRequest;
    }
}
