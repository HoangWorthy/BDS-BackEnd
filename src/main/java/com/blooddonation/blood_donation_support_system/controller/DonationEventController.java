//package com.blooddonation.blood_donation_support_system.controller;
//
//import com.blooddonation.blood_donation_support_system.dto.AccountDto;
//import com.blooddonation.blood_donation_support_system.dto.BulkBloodUnitRecordDto;
//import com.blooddonation.blood_donation_support_system.dto.DonationEventDto;
//import com.blooddonation.blood_donation_support_system.dto.UserDto;
//import com.blooddonation.blood_donation_support_system.enums.Role;
//import com.blooddonation.blood_donation_support_system.service.DonationEventService;
//import com.blooddonation.blood_donation_support_system.service.UserService;
//import com.blooddonation.blood_donation_support_system.util.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/donation-events")
//@CrossOrigin
//public class DonationEventController {
//    @Autowired
//    private DonationEventService donationEventService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping("/create")
//    public ResponseEntity<String> createDonationEvent(@RequestBody DonationEventDto donationEventDto,
//                                                      @CookieValue("jwt-token") String token) {
//        try {
//            AccountDto accountDto = jwtUtil.extractUser(token);
//            String result = donationEventService.createDonation(donationEventDto, accountDto.getEmail());
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while creating the donation event: ");
//        }
//    }
//
//    @PostMapping("/{eventId}/action")
//    public ResponseEntity<String> approveDonationEvent(@PathVariable Long eventId,
//                                                       @CookieValue("jwt-token") String token,
//                                                       @RequestParam String action) {
//        try {
//            UserDto userDto = jwtUtil.extractUser(token);
//            String result = donationEventService.verifyDonationEvent(eventId, userDto.getEmail(), action);
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while verifying the donation event: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/{eventId}/{timeSlotId}/register")
//    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId,
//                                                   @PathVariable Long timeSlotId,
//                                                   @CookieValue("jwt-token") String token) {
//        try {
//            UserDto userDto = jwtUtil.extractUser(token);
//            String result = donationEventService.registerForEvent(eventId, timeSlotId, userDto.getEmail());
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while registering for the event: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{eventId}")
//    public ResponseEntity<Object> getEventDetails(@PathVariable Long eventId) {
//        try {
//            DonationEventDto eventDetails = donationEventService.getDonationEventById(eventId);
//            return ResponseEntity.ok(eventDetails);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/donationList")
//    public ResponseEntity<List<DonationEventDto>> getAllDonationEvents() {
//        List<DonationEventDto> donationEvents = donationEventService.getAllDonationEvents();
//        return ResponseEntity.ok(donationEvents);
//    }
//
//    @GetMapping("/{startDate}/{endDate}/donationList")
//    public ResponseEntity<List<DonationEventDto>> getDonationEventsByDateRange(
//            @PathVariable LocalDate startDate,
//            @PathVariable LocalDate endDate) {
//        List<DonationEventDto> donationEvents = donationEventService.getEventByBetweenDates(startDate, endDate);
//        return ResponseEntity.ok(donationEvents);
//    }
//
//    @PostMapping("/{eventId}/record-donation")
//    public ResponseEntity<Object> recordDonation(
//            @PathVariable Long eventId,
//            @RequestBody BulkBloodUnitRecordDto bulkRecordDto,
//            @CookieValue("jwt-token") String token) {
//        try {
//            UserDto staff = jwtUtil.extractUser(token);
//            return ResponseEntity.ok(donationEventService.recordMultipleBloodDonations(eventId, bulkRecordDto.getSingleBloodUnitRecords(), staff.getEmail()));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error recording multiple donations: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{eventId}/{timeSlotId}/donors")
//    public ResponseEntity<List<UserDto>> getEventDonors(@PathVariable Long eventId,
//                                                        @PathVariable Long timeSlotId,
//                                                        @CookieValue("jwt-token") String token) {
//        UserDto staff = jwtUtil.extractUser(token);
//        return ResponseEntity.ok(donationEventService.getEventDonors(eventId, timeSlotId, staff.getEmail()));
//    }
//
//    @GetMapping("/{eventId}/qr-code")
//    public ResponseEntity<byte[]> getEventQRCode(@PathVariable Long eventId,
//                                                 @CookieValue("jwt-token") String token) {
//        try {
//            UserDto staff = jwtUtil.extractUser(token);
//            if (!staff.getRole().equals(Role.STAFF)) {
//                throw new RuntimeException("Unauthorized access");
//            }
//            DonationEventDto event = donationEventService.getDonationEventById(eventId);
//            if (event.getQrCode() == null) {
//                throw new RuntimeException("QR code not found for event: " + eventId);
//            }
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_PNG)
//                    .body(event.getQrCode());
//        } catch (RuntimeException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }
//
//    //need a frontend to test this
//    @PostMapping("/check-in/{eventId}")
//    public ResponseEntity<String> checkInToEvent(
//            @PathVariable Long eventId,
//            @RequestParam String registrationId,
//            @CookieValue("jwt-token") String token) {
//        try {
//            UserDto userDto = jwtUtil.extractUser(token);
//            String result = donationEventService.checkInMember(eventId, registrationId, userDto.getEmail());
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while checking-in the donation event: " + e.getMessage());
//        }
//    }
//
//}
//
//
