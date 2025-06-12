package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.AccountRoleUpdateDto;
import com.blooddonation.blood_donation_support_system.dto.AccountStatusUpdateDto;
import com.blooddonation.blood_donation_support_system.dto.UpdatePasswordDto;
import com.blooddonation.blood_donation_support_system.service.AccountService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AccountService accountService;

    // Change User password
    @PutMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@CookieValue("jwt-token") String jwtToken,
                                                 @Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            AccountDto updatedAccount = accountService.updateUserPassword(accountDto,
                    updatePasswordDto.getOldPassword(),
                    updatePasswordDto.getNewPassword());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating password");
        }
    }

    // Get User Account Info
    @GetMapping()
    public ResponseEntity<Object> account(@CookieValue(value = "jwt-token") String jwtToken) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            return ResponseEntity.ok(accountDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information");
        }
    }

    // Show a list of all accounts
    @GetMapping("/admin/accountList")
    public ResponseEntity<Page<AccountDto>> getAccountList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Page<AccountDto> accountDtoList = accountService.getAllAccounts(page, size, sortBy, ascending);
        return ResponseEntity.ok(accountDtoList);
    }

    // Update a user's role
    @PutMapping("/admin/{accountId}/role")
    public ResponseEntity<Object> updateAccountRole(
            @PathVariable Long accountId,
            @RequestBody AccountRoleUpdateDto roleUpdate) {
        try {
            AccountDto updatedAccount = accountService.updateUserRole(accountId, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update a user's status(enable/disable)
    @PutMapping("/admin/{accountId}/status")
    public ResponseEntity<Object> updateAccountStatus(@PathVariable Long accountId,
                                                      @Valid @RequestBody AccountStatusUpdateDto statusUpdate) {
        try {
            AccountDto updatedAccount = accountService.updateUserStatus(accountId, statusUpdate.getStatus().name());
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Show specific account
    @GetMapping("/admin/{accountId}")
    public ResponseEntity<Object> getAccountById(@PathVariable Long accountId) {
        try {
            AccountDto accountDto = accountService.getAccountById(accountId);
            return ResponseEntity.ok(accountDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving user information by email");
        }
    }
}
