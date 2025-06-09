package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.service.AdminService;
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
    @PutMapping("/user/{accountId}/role")
    public ResponseEntity<Object> updateAccountRole(
            @PathVariable Long accountId,
            @RequestBody AccountDto roleUpdate) {
        try {
            AccountDto updatedAccount = adminService.updateUserRole(accountId, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/user/{accountId}/status")
    public ResponseEntity<Object> updateAccountStatus(@PathVariable Long accountId,
                                                      @RequestBody AccountDto statusUpdate) {
        try {
        AccountDto updatedAccount = adminService.updateUserStatus(accountId, statusUpdate.getStatus().name());
        return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Object> getAccountById(@PathVariable Long accountId) {
        try {
            AccountDto accountDto = adminService.getAccountById(accountId);
            return ResponseEntity.ok(accountDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information by email");
        }
    }

    @GetMapping("/profile/{accountId}")
    public ResponseEntity<Object> getProfileById(@PathVariable Long accountId) {
        try {
            ProfileDto profileDto = adminService.getProfileById(accountId);
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user profile by email");
        }
    }

}
