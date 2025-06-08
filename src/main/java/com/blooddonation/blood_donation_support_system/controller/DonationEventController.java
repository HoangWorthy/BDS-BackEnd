package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.BulkBloodUnitRecordDto;
import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.service.CheckinTokenService;
import com.blooddonation.blood_donation_support_system.service.DonationEventService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/donation-events")
@CrossOrigin
public class DonationEventController {
    @Autowired
    private DonationEventService donationEventService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CheckinTokenService checkinTokenService;

    @PostMapping("/create")
    public ResponseEntity<String> createDonationEvent(@RequestBody DonationEventDto donationEventDto,
                                                      @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventService.createDonation(donationEventDto, accountDto.getEmail());
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
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventService.verifyDonationEvent(eventId, accountDto.getEmail(), action);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while verifying the donation event: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/{timeSlotId}/register")
    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId,
                                                   @PathVariable Long timeSlotId,
                                                   @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventService.registerForEventOnline(eventId, timeSlotId, accountDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering for the event: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/registerOffline")
    public ResponseEntity<String> registerForEventOffline(
            @PathVariable Long eventId,
            @RequestParam String personalId,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventService.registerForEventOffline(eventId, personalId, accountDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering for the event: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/register-guest")
    public ResponseEntity<Object> registerGuest(
            @PathVariable Long eventId,
            @RequestBody ProfileDto profileDto,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto staff = jwtUtil.extractUser(token);
            ProfileDto registeredProfile = donationEventService.registerForGuest(eventId, profileDto, staff.getEmail());
            return ResponseEntity.ok(registeredProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering guest: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<String> cancelDonationEvent(@PathVariable Long eventId,
                                                      @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            String result = donationEventService.cancelEventRegistration(eventId, accountDto.getEmail());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while cancelling the donation event: " + e.getMessage());
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

    @GetMapping("/{startDate}/{endDate}/donationList")
    public ResponseEntity<List<DonationEventDto>> getDonationEventsByDateRange(
            @PathVariable LocalDate startDate,
            @PathVariable LocalDate endDate) {
        List<DonationEventDto> donationEvents = donationEventService.getEventByBetweenDates(startDate, endDate);
        return ResponseEntity.ok(donationEvents);
    }

    @PostMapping("/{eventId}/record-donation")
    public ResponseEntity<Object> recordDonation(
            @PathVariable Long eventId,
            @RequestBody BulkBloodUnitRecordDto bulkRecordDto,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto staff = jwtUtil.extractUser(token);
            return ResponseEntity.ok(donationEventService.recordMultipleBloodDonations(eventId, bulkRecordDto.getSingleBloodUnitRecords(), staff.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error recording multiple donations: " + e.getMessage());
        }
    }

    @GetMapping("/{eventId}/{timeSlotId}/donors")
    public ResponseEntity<List<AccountDto>> getEventDonors(@PathVariable Long eventId,
                                                           @PathVariable Long timeSlotId,
                                                           @CookieValue("jwt-token") String token) {
        AccountDto staff = jwtUtil.extractUser(token);
        return ResponseEntity.ok(donationEventService.getEventDonors(eventId, timeSlotId, staff.getEmail()));
    }

    @GetMapping("/{eventId}/qr-code")
    public ResponseEntity<Object> getUserQRCode(
            @PathVariable Long eventId,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            byte[] qrCode = donationEventService.getQRCodeForUser(eventId, accountDto.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                    .body(qrCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving QR Code");

        }
    }

    @GetMapping("/check-in/info/{eventId}")
    public ResponseEntity<Object> checkInInfo(@PathVariable Long eventId,
                                              @RequestParam String checkinToken,
                                              @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            ProfileDto profileDto = checkinTokenService.getProfileFromToken(checkinToken, accountDto.getEmail());
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information");
        }
    }

    @PostMapping("/check-in/action/{eventId}")
    public ResponseEntity<String> checkInToEvent(
            @PathVariable Long eventId,
            @RequestParam String action,
            @RequestParam String checkinToken,
            @CookieValue("jwt-token") String token) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(token);
            ProfileDto profileDto = checkinTokenService.getProfileFromToken(checkinToken, accountDto.getEmail());
            String result = donationEventService.checkInMember(eventId, action, accountDto.getEmail(), profileDto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while checking-in the donation event: " + e.getMessage());
        }
    }
}


