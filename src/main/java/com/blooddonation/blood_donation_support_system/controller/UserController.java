package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.*;
import com.blooddonation.blood_donation_support_system.service.TokenBlacklistService;
import com.blooddonation.blood_donation_support_system.service.UserService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    //Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AccountDto accountDto, HttpServletResponse response) {
        String jwtToken = userService.login(accountDto);
        if (jwtToken == null || jwtToken.equals("Invalid email or password")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        }

        Cookie cookie = new Cookie("jwt-token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS only — use false for localhost HTTP dev
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setDomain("localhost"); // Optional, but helps in some setups
        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful");
    }

    // Logout
    @GetMapping("/logout")
    public void logout(@CookieValue("jwt-token") String token, HttpServletResponse response) throws IOException, java.io.IOException {
        tokenBlacklistService.blacklistToken(token);
        Cookie cookie = new Cookie("jwt-token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        response.sendRedirect("/login");
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AccountDto accountDto) {
        String result = userService.registerUser(accountDto);
        if (result.equals("Email already exists") || result.contains("empty")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    //Verify User email before login
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

    // Resend Verification Code if they don't receive it
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        String result = userService.resendVerificationCode(email);
        if (result.equals("No temporary registration found for this email")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Initialize Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String result = userService.initiatePasswordReset(email);
        if (result.equals("Email not found")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Reset Password using the code sent to the email
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        String result = userService.resetPassword(resetPasswordDto.getCode(), resetPasswordDto.getNewPassword());
        if (result.equals("Reset code invalid") ||
                result.equals("Reset code expired") ||
                result.equals("Invalid reset code")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // Update User Profile
    @PutMapping("/update")
    public ResponseEntity<Object> update(@CookieValue("jwt-token") String jwtToken,
                                       @RequestBody ProfileDto profileDto) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            ProfileDto updatedAccount = userService.updateUser(accountDto, profileDto);
            if (updatedAccount == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating user information");
        }
    }
    // Update Account Password
    @PutMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@CookieValue("jwt-token") String jwtToken,
                                                 @RequestBody UpdatePasswordDto updatePasswordDto) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            AccountDto updatedAccount = userService.updateUserPassword(accountDto,
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
    @GetMapping("/account")
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

    // Get User Profile Info
    @GetMapping("/profile")
    public ResponseEntity<Object> profile(@CookieValue(value = "jwt-token") String jwtToken) {
        try {
            AccountDto accountDto = jwtUtil.extractUser(jwtToken);
            ProfileDto profileDto = userService.getProfileByEmail(accountDto.getEmail());
            return ResponseEntity.ok(profileDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving profile information");
        }
    }
}

