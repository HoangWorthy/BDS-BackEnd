package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.mapper.UserMapper;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, User> temporaryUsers = new HashMap<>();
    private final Map<String, LocalDateTime> codeExpiration = new HashMap<>();

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UserDto currentUser, UserDto updatedUser) {
        User userEntity = UserMapper.mapToUser(currentUser);

        userEntity.setPhone(updatedUser.getPhone());
        userEntity.setAddress(updatedUser.getAddress());
        userEntity.setBloodType(updatedUser.getBloodType());
        userEntity.setGender(updatedUser.getGender());
        userEntity.setDateOfBirth(updatedUser.getDateOfBirth());
        userEntity.setLastDonationDate(updatedUser.getLastDonationDate());
        userEntity.setPersonalId(updatedUser.getPersonalId());

        User savedUser = userRepository.save(userEntity);
        return UserMapper.mapToUserDto(savedUser);
    }

    public String registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            return "Email already exists";
        } else if (userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()) {
            return "Email and password cannot be empty";
        }
        // Encode the password and saving
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User userEntity = UserMapper.mapToUser(userDto);

        // Remove existing temporary registration for this email if exists
        String existingCode = temporaryUsers.entrySet().stream()
                .filter(entry -> entry.getValue().getEmail().equals(userDto.getEmail()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (existingCode != null) {
            temporaryUsers.remove(existingCode);
            codeExpiration.remove(existingCode);
        }

        // Generate a verification code and store it temporarily
        String verificationCode = generateVerificationCode();
        temporaryUsers.put(verificationCode, userEntity);
        codeExpiration.put(verificationCode, LocalDateTime.now().plusMinutes(10));

        // Send verification email
        sendVerificationEmail(userEntity, verificationCode);
        return "verification email sent";
    }

    public String resendVerificationCode(String email) {
        User userEntity = temporaryUsers.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (userEntity == null) {
            return "No temporary registration found for this email";
        }

        // Remove old verification code
        String oldCode = temporaryUsers.entrySet().stream()
                .filter(entry -> entry.getValue().getEmail().equals(email))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (oldCode != null) {
            temporaryUsers.remove(oldCode);
            codeExpiration.remove(oldCode);
        }

        String newVerificationCode = generateVerificationCode();
        temporaryUsers.put(newVerificationCode, userEntity);
        codeExpiration.put(newVerificationCode, LocalDateTime.now().plusMinutes(10));

        sendVerificationEmail(userEntity, newVerificationCode);
        return "New verification email sent";
    }

    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private void sendVerificationEmail(User user, String verificationCode) {
        String subject = "Account Verification";
        String htmlMessage = "<html>"
                + "<body>"
                + "<h2>Welcome to Blood Donation Support System!</h2>"
                + "<p>Please use the following code to verify your account:</p>"
                + "<h3>" + verificationCode + "</h3>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String verifyUser(String code) {
        if (!codeExpiration.containsKey(code)) {
            return "Verification code invalid";
        }
        if (LocalDateTime.now().isAfter(codeExpiration.get(code))) {
            return "Verification code expired";
        }
        User user = temporaryUsers.get(code);
        if (user != null) {
            userRepository.save(user);
            temporaryUsers.remove(code);
            codeExpiration.remove(code);
            return "User registered successfully";

        }
        return "Invalid verification code";
    }

    @Scheduled(fixedDelay = 600000)
    public void cleanupExpiredRegistrations() {
        LocalDateTime now = LocalDateTime.now();
        codeExpiration.entrySet().removeIf(entry -> {
            if (now.isAfter(entry.getValue())) {
                temporaryUsers.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}

