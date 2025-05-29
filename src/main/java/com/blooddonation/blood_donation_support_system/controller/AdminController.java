package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.service.AdminService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<UserDto>> getUserList(@CookieValue("jwt-token") String token) {
            List<UserDto> userList = adminService.getAllUsers();
            return ResponseEntity.ok(userList);
    }

    // Show a specific user by email
    @GetMapping("/user/{email}")
    public ResponseEntity<UserDto> getUser(@CookieValue("jwt-token") String token, @PathVariable String email) {
            UserDto user = adminService.getUserByEmail(email);
            return ResponseEntity.ok(user);
    }

    // Update a user's role
    @PutMapping("/user/{email}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @CookieValue("jwt-token") String token,
            @PathVariable String email,
            @RequestBody UserDto roleUpdate) {
            UserDto updatedUser = adminService.updateUserRole(email, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedUser);
    }

}
