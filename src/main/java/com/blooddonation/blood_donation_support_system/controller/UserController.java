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
        UserDto userDto = jwtUtil.extractUser(jwtToken);
        return ResponseEntity.ok(userDto);
    }

    // Update User Info
    @PutMapping("/update")
    public ResponseEntity<UserDto> update(@CookieValue("jwt-token") String jwtToken,
                                          @RequestBody UserDto updatedUser) {
        UserDto userDto = jwtUtil.extractUser(jwtToken);
        UserDto updatedUserEntity = userService.updateUser(userDto, updatedUser);
        return ResponseEntity.ok(updatedUserEntity);
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
    public String registerUser(@RequestBody UserDto userDto) {
        return userService.registerUser(userDto);
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String verificationCode) {
        return userService.verifyUser(verificationCode);
    }

//    @PostMapping("/verify")
//    public ResponseEntity<String> verify(@RequestBody VerificationDto verificationDto) {
//        String result = userService.verifyUser(verificationDto.getCode());
//        return ResponseEntity.ok(result);
//    }
}