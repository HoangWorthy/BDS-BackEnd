package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.BulkBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.dto.SingleBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.service.DonationEventService;
import com.blooddonation.blood_donation_support_system.service.UserService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donation-events")
@CrossOrigin
public class DonationEventController {
    @Autowired
    private DonationEventService donationEventService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<String> createDonationEvent(@RequestBody DonationEventDto donationEventDto,
                                                      @CookieValue("jwt-token") String token) {
        try {
            UserDto userDto = jwtUtil.extractUser(token);
            String result = donationEventService.createDonation(donationEventDto, userDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the donation event: ");
        }
    }

    @PostMapping("/{eventId}/action")
    public ResponseEntity<String> approveDonationEvent(@PathVariable Long eventId,
                                                       @CookieValue("jwt-token") String token,
                                                       @RequestParam String action) {
        try {
            UserDto userDto = jwtUtil.extractUser(token);
            String result = donationEventService.verifyDonationEvent(eventId, userDto.getEmail(), action);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while verifying the donation event: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId,
                                                   @CookieValue("jwt-token") String token) {
        try {
            UserDto userDto = jwtUtil.extractUser(token);
            String result = donationEventService.registerForEvent(eventId, userDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering for the event: " + e.getMessage());
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> getEventDetails(@PathVariable Long eventId) {
        try {
            DonationEventDto eventDetails = donationEventService.getDonationEventById(eventId);
            return ResponseEntity.ok(eventDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/donationList")
    public ResponseEntity<List<DonationEventDto>> getAllDonationEvents() {
        List<DonationEventDto> donationEvents = donationEventService.getAllDonationEvents();
        return ResponseEntity.ok(donationEvents);
    }

    @PostMapping("/{eventId}/record-donation")
    public ResponseEntity<Object> recordDonation(
            @PathVariable Long eventId,
            @RequestBody BulkBloodUnitRecordDto bulkRecordDto,
            @CookieValue("jwt-token") String token) {
        try {
            UserDto staff = jwtUtil.extractUser(token);
            return ResponseEntity.ok(donationEventService.recordMultipleBloodDonations(eventId, bulkRecordDto.getSingleBloodUnitRecords()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error recording multiple donations: " + e.getMessage());
        }
    }

    @GetMapping("/{eventId}/donors")
    public ResponseEntity<List<UserDto>> getEventDonors(@PathVariable Long eventId) {
        return ResponseEntity.ok(donationEventService.getEventDonors(eventId));
    }
}

