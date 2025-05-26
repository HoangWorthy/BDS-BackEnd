package com.blooddonation.blood_donation_support_system.controller;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.service.UserService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Get User Info
    @GetMapping("/info")
    public ResponseEntity<UserDto> info(@CookieValue(value = "jwt-token") String jwtToken) {
        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            UserDto userDto = jwtUtil.extractUser(jwtToken);
//            UserDto userDto = userService.getUserByEmail(userEmail);
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Update User Info
    @PutMapping("/update")
    public ResponseEntity<UserDto> update(@CookieValue("jwt-token") String jwtToken,
                                          @RequestBody UserDto updatedUser) {
        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            UserDto userDto = jwtUtil.extractUser(jwtToken);
            UserDto updatedUserEntity = userService.updateUser(userDto, updatedUser);
            return ResponseEntity.ok(updatedUserEntity);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

}