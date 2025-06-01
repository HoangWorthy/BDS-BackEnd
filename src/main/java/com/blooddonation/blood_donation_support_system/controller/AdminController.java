package com.blooddonation.blood_donation_support_system.controller;

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

    @Autowired
    private JwtUtil jwtUtil;

    // Show a list of all users
    @GetMapping("/userList")
    public ResponseEntity<List<UserDto>> getUserList() {
        List<UserDto> userList = adminService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    // Show a specific user by email
    @GetMapping("/user/{email}")
    public ResponseEntity<Object> getUser(@PathVariable String email) {
        try {
            UserDto user = adminService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update a user's role
    @PutMapping("/user/{email}/role")
    public ResponseEntity<Object> updateUserRole(
            @PathVariable String email,
            @RequestBody UserDto roleUpdate) {
        try {
            UserDto updatedUser = adminService.updateUserRole(email, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
