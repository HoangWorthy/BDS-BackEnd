package com.blooddonation.blood_donation_support_system.util;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.exception.OAuth2AttributeException;
import com.blooddonation.blood_donation_support_system.mapper.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.blooddonation.blood_donation_support_system.entity.Role.MEMBER;

@Component
public class JwtUtil {

    private final UserRepository userRepository;
    private final SecretKey secretKey;
    private final long tokenAge = 1000 * 60 * 60;

    public JwtUtil(UserRepository userRepository, @Value("${jwt.secret}") String secret) {
        this.userRepository = userRepository;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenAge))
                .signWith(secretKey)
                .compact();
    }

    // This method is called when the user successfully logs in using OAuth2 and save the user
    public String saveOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        //throw exception github does not share email (private email)
        if (email == null) {
            throw new OAuth2AttributeException("Email is required for authentication. Please ensure your OAuth2 provider shares your email.");
        }
        String name = oAuth2User.getAttribute("name");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(MEMBER);
            userRepository.save(newUser);
        }
        return generateToken(email);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expiration.before(new Date(System.currentTimeMillis()));
    }

    public String extractBody(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public UserDto extractUser(String token) {
        String email = extractBody(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return UserMapper.mapToUserDto(user);
    }


    public boolean validateToken(String token) {
        return (!isTokenExpired(token));
    }
}
