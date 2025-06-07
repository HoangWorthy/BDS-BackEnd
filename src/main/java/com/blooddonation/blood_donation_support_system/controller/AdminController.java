package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.service.AdminService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    // Show a list of all accounts
    @GetMapping("/accountList")
    public ResponseEntity<List<AccountDto>> getAccountList() {
        List<AccountDto> accountDtoList = adminService.getAllAccounts();
        return ResponseEntity.ok(accountDtoList);
    }

    // Show a list of all profiles
    @GetMapping("/profileList")
    public ResponseEntity<List<ProfileDto>> getProfileList() {
        List<ProfileDto> profileDtoList = adminService.getAllProfiles();
        return ResponseEntity.ok(profileDtoList);
    }

    // Update a user's role
    @PutMapping("/user/{email}/role")
    public ResponseEntity<Object> updateAccountRole(
            @PathVariable String email,
            @RequestBody AccountDto roleUpdate) {
        try {
            AccountDto updatedAccount = adminService.updateUserRole(email, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/account/{email}")
    public ResponseEntity<Object> getAccountByEmail(@PathVariable String email) {
        try {
            AccountDto accountDto = adminService.getAccountByEmail(email);
            return ResponseEntity.ok(accountDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information by email");
        }
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<Object> getProfileByEmail(@PathVariable String email) {
        try {
            ProfileDto profileDto = adminService.getProfileByEmail(email);
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user profile by email");
        }
    }

}
