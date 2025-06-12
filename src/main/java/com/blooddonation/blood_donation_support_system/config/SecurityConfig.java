package com.blooddonation.blood_donation_support_system.config;

import com.blooddonation.blood_donation_support_system.filter.JwtFilter;
import com.blooddonation.blood_donation_support_system.service.OAuth2LoginSuccessHandler;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/account/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/account/**").hasAnyRole("MEMBER", "ADMIN", "STAFF")
                        .requestMatchers("/api/profile/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/profile/**").hasAnyRole("MEMBER", "ADMIN", "STAFF")
                        .requestMatchers("/api/checkin/{eventId}/qr-code").hasRole("MEMBER")
                        .requestMatchers("/api/checkin/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/event-registration/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/event-registration/**").hasAnyRole("MEMBER", "ADMIN", "STAFF")
                        .requestMatchers("/api/donation-event/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/donation-event/{eventId}/status").hasRole("ADMIN")
                        .requestMatchers("/api/donation-event/**").permitAll()
                        .requestMatchers("/api/medical-facility-stock/**").hasRole("STAFF")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler())
                );


        return http.build();
    }
}