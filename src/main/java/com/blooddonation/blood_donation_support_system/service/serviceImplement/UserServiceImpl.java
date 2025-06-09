package com.blooddonation.blood_donation_support_system.service.serviceImplement;

import com.blooddonation.blood_donation_support_system.dto.AccountDto;
import com.blooddonation.blood_donation_support_system.dto.ProfileDto;
import com.blooddonation.blood_donation_support_system.dto.UserDonationHistoryDto;
import com.blooddonation.blood_donation_support_system.entity.Account;
import com.blooddonation.blood_donation_support_system.entity.EventRegistration;
import com.blooddonation.blood_donation_support_system.entity.Profile;
import com.blooddonation.blood_donation_support_system.enums.Role;
import com.blooddonation.blood_donation_support_system.enums.Status;
import com.blooddonation.blood_donation_support_system.mapper.AccountMapper;
import com.blooddonation.blood_donation_support_system.mapper.ProfileMapper;
import com.blooddonation.blood_donation_support_system.mapper.UserDonationHistoryMapper;
import com.blooddonation.blood_donation_support_system.repository.AccountRepository;
import com.blooddonation.blood_donation_support_system.repository.EventRegistrationRepository;
import com.blooddonation.blood_donation_support_system.repository.ProfileRepository;
import com.blooddonation.blood_donation_support_system.service.EmailService;
import com.blooddonation.blood_donation_support_system.service.UserService;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import com.blooddonation.blood_donation_support_system.validator.UserValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    @Autowired
    private UserDonationHistoryMapper userDonationHistoryMapper;
    @Autowired
    private UserValidator validator;

    private final Map<String, Account> temporaryUsers = new HashMap<>();
    private final Map<String, LocalDateTime> codeExpiration = new HashMap<>();

    public String registerUser(AccountDto accountDto) {
        if (accountRepository.findByEmail(accountDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        } else if (accountDto.getEmail().isEmpty() || accountDto.getPassword().isEmpty()) {
            throw new RuntimeException("email and Password cannot be empty");
        }
        // Encode the password
        accountDto.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        accountDto.setRole(Role.MEMBER);
        accountDto.setStatus(Status.ENABLE);
        Account account = AccountMapper.toEntity(accountDto);

        removeOldCode(accountDto.getEmail());

        // Generate a verification code and store it temporarily
        String verificationCode = generateVerificationCode();
        temporaryUsers.put(verificationCode, account);
        codeExpiration.put(verificationCode, LocalDateTime.now().plusMinutes(10));

        // Send verification email
        sendVerificationEmail(account, verificationCode);
        return "verification email sent";
    }

    public String resendVerificationCode(String email) {
        Account account = temporaryUsers.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if (account == null) {
            return "No temporary registration found for this email";
        }

        // Remove old verification code
        removeOldCode(email);

        String newVerificationCode = generateVerificationCode();
        temporaryUsers.put(newVerificationCode, account);
        codeExpiration.put(newVerificationCode, LocalDateTime.now().plusMinutes(10));

        sendVerificationEmail(account, newVerificationCode);
        return "New verification email sent";
    }

    @Transactional
    public String verifyUser(String code) {
        if (!codeExpiration.containsKey(code)) {
            return "Verification code invalid";
        }
        if (LocalDateTime.now().isAfter(codeExpiration.get(code))) {
            return "Verification code expired";
        }

        Account account = temporaryUsers.get(code);
        if (account != null) {
            // Create and set profile
            Profile profile = new Profile();
            account.setProfile(profile);

            // Save account to get generated ID
            Account savedAccount = accountRepository.save(account);

            // Set the account ID in the profile and save it
            Profile updatedProfile = savedAccount.getProfile();
            updatedProfile.setAccountId(savedAccount.getId());
            profileRepository.save(updatedProfile);

            temporaryUsers.remove(code);
            codeExpiration.remove(code);
            return "User registered successfully";
        }
        return "Invalid verification code";
    }

    public String login(AccountDto accountDto) {
        Account account = validator.getUserOrThrow(accountDto.getId());
        validator.validateLogin(account, accountDto);
        return jwtUtil.generateToken(accountDto.getEmail());
    }

    @Transactional
    public ProfileDto updateUser(AccountDto accountDto, ProfileDto profileDto) {
        Account account = validator.getUserOrThrow(accountDto.getId());
        Profile profile = validator.getProfileOrThrow(account.getProfile());

        ProfileMapper.updateEntityFromDto(profile, profileDto);

        Profile updatedProfile = profileRepository.save(profile);
        return ProfileMapper.toDto(updatedProfile);
    }



    public AccountDto updateUserPassword(AccountDto accountDto, String oldPassword, String newPassword) {
        Account account = validator.getUserOrThrow(accountDto.getId());
        validator.validateUpdatePassword(oldPassword, account.getPassword(), newPassword);
        account.setPassword(passwordEncoder.encode(newPassword));
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.toDto(savedAccount);
    }


    public String initiatePasswordReset(String email) {
        Account account = validator.getEmailOrThrow(email);

        removeOldCode(email);

        String resetCode = generateVerificationCode();
        codeExpiration.put(resetCode, LocalDateTime.now().plusMinutes(10));
        temporaryUsers.put(resetCode, account);
        sendResetPassword(account, resetCode);
        return "New password reset code sent";
    }

    public String resetPassword(String resetCode, String newPassword) {
        if (!codeExpiration.containsKey(resetCode)) {
            return "Reset code invalid";
        }
        if (LocalDateTime.now().isAfter(codeExpiration.get(resetCode))) {
            return "Reset code expired";
        }

        Account account = temporaryUsers.get(resetCode);
        if (account == null) {
            return "Invalid reset code";
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        // Clean up temporary data
        temporaryUsers.remove(resetCode);
        codeExpiration.remove(resetCode);

        return "Password reset successfully";
    }

    public ProfileDto getProfileById(Long accountId) {
        Account account = validator.getUserOrThrow(accountId);
        Profile profile = validator.getProfileOrThrow(account.getProfile());
        return ProfileMapper.toDto(profile);
    }

    @Transactional
    public List<UserDonationHistoryDto> getDonationHistory(Long accountId) {
        Account account = validator.getUserOrThrow(accountId);
        List<EventRegistration> registrations = eventRegistrationRepository.findByAccount(account);
        return userDonationHistoryMapper.toDtoList(registrations);
    }


    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private void removeOldCode(String email) {
        String oldCode = temporaryUsers.entrySet().stream()
                .filter(entry -> entry.getValue().getEmail().equals(email))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (oldCode != null) {
            temporaryUsers.remove(oldCode);
            codeExpiration.remove(oldCode);
        }
    }

    private void sendVerificationEmail(Account account, String verificationCode) {
        String subject = "Account Verification";
        String htmlMessage = "<html>"
                + "<body>"
                + "<h2>Welcome to Blood Donation Support System!</h2>"
                + "<p>Please use the following code to verify your account:</p>"
                + "<h3>" + verificationCode + "</h3>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(account.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResetPassword(Account account, String resetCode) {
        String subject = "Password Reset Request";
        String htmlMessage = "<html>"
                + "<body>"
                + "<h2>Password Reset Request</h2>"
                + "<p>Your password reset code is:</p>"
                + "<h3>" + resetCode + "</h3>"
                + "<p>This code will expire in 10 minutes.</p>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(account.getEmail(), subject, htmlMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

