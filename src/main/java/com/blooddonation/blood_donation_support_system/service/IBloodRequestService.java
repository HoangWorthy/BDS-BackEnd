package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IBloodRequestService {
    BloodRequestDto createBloodRequest(BloodRequestDto bloodRequestDto);
    List<BloodRequestDto> findBloodRequestByName(String name);
    List<BloodRequestDto> findAllBloodRequest();
    BloodRequestDto findBloodRequestById(int id);
}
