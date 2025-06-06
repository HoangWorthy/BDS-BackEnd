//package com.blooddonation.blood_donation_support_system.controller;
//
//import com.blooddonation.blood_donation_support_system.dto.UserDto;
//import com.blooddonation.blood_donation_support_system.enums.BloodType;
//import com.blooddonation.blood_donation_support_system.service.MedicalFacilityStockService;
//import com.blooddonation.blood_donation_support_system.util.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/medical-facility-stock")
//public class MedicalFacilityStockController {
//
//    @Autowired
//    private MedicalFacilityStockService medicalFacilityStockService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping("/add-from-event/{eventId}")
//    public ResponseEntity<String> addBloodUnitsFromEvent(@PathVariable Long eventId,
//                                                         @CookieValue("jwt-token") String token) {
//        try {
//            UserDto userDto = jwtUtil.extractUser(token);
//
//            String result = medicalFacilityStockService.addBloodUnitsToStockByEventId(eventId, userDto.getEmail());
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while adding blood units to stock from event: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/divide-whole-blood")
//    public ResponseEntity<String> divideWholeBlood(@RequestBody String bloodUnitId,
//                                                   @RequestParam BloodType bloodType,
//                                                   @RequestParam Double amount,
//                                                   @CookieValue("jwt-token") String token) {
//        try {
//            UserDto userDto = jwtUtil.extractUser(token);
//            String result = medicalFacilityStockService.divideWholeBloodInStock(bloodType, amount, userDto.getEmail());
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while dividing whole blood: " + e.getMessage());
//        }
//    }
//}