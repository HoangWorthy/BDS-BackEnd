package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;

import java.util.List;

public interface IBloodRequestService {
    BloodRequestDto createBloodRequest(BloodRequestDto bloodRequestDto);
    List<BloodRequestDto> findBloodRequestByName(String name);
    List<BloodRequestDto> findAllBloodRequest();
}
