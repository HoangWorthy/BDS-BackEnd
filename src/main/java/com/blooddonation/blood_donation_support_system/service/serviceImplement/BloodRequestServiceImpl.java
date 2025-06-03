package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.entity.BloodRequest;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.BloodRequestMapper;
import com.blooddonation.blood_donation_support_system.repository.BloodRequestRepository;
import com.blooddonation.blood_donation_support_system.service.IBloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodRequestServiceImpl implements IBloodRequestService {
    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Override
    public BloodRequestDto createBloodRequest(BloodRequestDto bloodRequestDto) {
        if (findBloodRequestByName(bloodRequestDto.getName()) != null) {
           return null;
        }
        bloodRequestDto.setStatus(Status.PENDING);
        bloodRequestRepository.save(BloodRequestMapper.toBloodRequestEntity(bloodRequestDto));
        return bloodRequestDto;
    }

    @Override
    public List<BloodRequestDto> findBloodRequestByName(String name) {
        return List.of();
    }

    @Override
    public List<BloodRequestDto> findAllBloodRequest() {
        List<BloodRequest> bloodRequests = bloodRequestRepository.findAll();
        return bloodRequests.stream().map(BloodRequestMapper::toBloodRequestDto)
                .collect(Collectors.toList());
    }
}
