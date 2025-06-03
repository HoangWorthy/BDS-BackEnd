package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.BloodRequestDto;
import com.blooddonation.blood_donation_support_system.service.IBloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blood-request")
public class BloodRequestController {
    @Autowired
    private IBloodRequestService iBloodRequestService;

    @GetMapping("/request-list")
    public ResponseEntity<List<BloodRequestDto>> getAllRequest() {
        return new ResponseEntity<>(iBloodRequestService.findAllBloodRequest(), HttpStatus.OK);
    }

    @PostMapping("/create-request")
    public ResponseEntity<BloodRequestDto> createRequest(@RequestBody BloodRequestDto bloodRequestDto) {
        return new ResponseEntity<>(iBloodRequestService.createBloodRequest(bloodRequestDto), HttpStatus.CREATED);
    }
}
