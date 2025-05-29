package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.dto.VerificationDto;
import com.blooddonation.blood_donation_support_system.service.UserService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // Get User Info
    @GetMapping("/info")
    public ResponseEntity<UserDto> info(@CookieValue(value = "jwt-token") String jwtToken) {
        try {
            UserDto userDto = jwtUtil.extractUser(jwtToken);
            if (userDto == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Update User Info
    @PutMapping("/update")
    public ResponseEntity<UserDto> update(@CookieValue("jwt-token") String jwtToken,
                                          @RequestBody UserDto updatedUser) {
        try {
            UserDto userDto = jwtUtil.extractUser(jwtToken);
            UserDto updatedUserEntity = userService.updateUser(userDto, updatedUser);
            if (updatedUserEntity == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(updatedUserEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // This endpoint use to invalidate the JWT token
    @GetMapping("/logout")
    public void logout(HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        response.sendRedirect("/login");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        String result = userService.registerUser(userDto);
        if (result.equals("Email already exists") || result.contains("empty")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String verificationCode) {
        String result = userService.verifyUser(verificationCode);
        if (result.equals("Verification code invalid") ||
                result.equals("Verification code expired") ||
                result.equals("Invalid verification code")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        String result = userService.resendVerificationCode(email);
        if (result.equals("No temporary registration found for this email")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

//    @PostMapping("/verify")
//    public ResponseEntity<String> verify(@RequestBody VerificationDto verificationDto) {
//        String result = userService.verifyUser(verificationDto.getCode());
//        return ResponseEntity.ok(result);
//    }
}