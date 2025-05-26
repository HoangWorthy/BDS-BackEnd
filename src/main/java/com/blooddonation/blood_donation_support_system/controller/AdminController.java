package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.Role;
import com.blooddonation.blood_donation_support_system.service.AdminService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    // Show a list of all users
    @GetMapping("/userList")
    public ResponseEntity<List<UserDto>> getUserList(@CookieValue("jwt-token") String token) {
        if (token != null && jwtUtil.validateToken(token) && jwtUtil.extractUser(token).getRole() == Role.ADMIN) {
            List<UserDto> userList = adminService.getAllUsers();
            return ResponseEntity.ok(userList);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Show a specific user by email
    @GetMapping("/user/{email}")
    public ResponseEntity<UserDto> getUser(@CookieValue("jwt-token") String token, @PathVariable String email) {
        if (token != null && jwtUtil.validateToken(token) && jwtUtil.extractUser(token).getRole() == Role.ADMIN) {
            UserDto user = adminService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Update a user's role
    @PutMapping("/user/{email}/role")
    public ResponseEntity<UserDto> updateUserRole(
            @CookieValue("jwt-token") String token,
            @PathVariable String email,
            @RequestBody UserDto roleUpdate) {
        if (token != null && jwtUtil.validateToken(token) && jwtUtil.extractUser(token).getRole() == Role.ADMIN) {
            UserDto updatedUser = adminService.updateUserRole(email, roleUpdate.getRole().name());
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
