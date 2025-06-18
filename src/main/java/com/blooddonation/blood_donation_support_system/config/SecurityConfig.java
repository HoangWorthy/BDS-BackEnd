package com.blooddonation.blood_donation_support_system.config;

import com.blooddonation.blood_donation_support_system.filter.JwtFilter;
import com.blooddonation.blood_donation_support_system.service.OAuth2LoginSuccessHandler;
import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
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
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"Authentication failed\":" + ex);
                        })
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"Access denied\":\"You don't have the required role to access this resource\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/account/list-account", "/api/user/account/{accountId}/role", "/api/user/account/{accountId}/status", "/api/user/account/list-account/{accountId}").hasRole("ADMIN")
                        .requestMatchers("/api/user/account/**").hasAnyRole("MEMBER", "ADMIN", "STAFF")
                        .requestMatchers("/api/user/profile/list-profile/{accountId}", "/api/user/profile/list-profile", "/api/user/profile/list-profile/{accountId}/history").hasRole("ADMIN")
                        .requestMatchers("/api/user/profile/**").hasAnyRole("MEMBER", "ADMIN", "STAFF")
                        .requestMatchers("/api/checkin/{eventId}/qr-code").hasRole("MEMBER")
                        .requestMatchers("/api/checkin/info/{eventId}","/api/checkin/action/{eventId}").hasRole("STAFF")
                        .requestMatchers("/api/event-registration/{eventId}/registerOffline", "/api/event-registration/{eventId}/register-guest").hasRole("STAFF")
                        .requestMatchers("/api/event-registration/**").hasAnyRole("MEMBER", "ADMIN", "STAFF")
                        .requestMatchers("/api/donation-event/create", "/api/donation-event/list-donation/{eventId}/record-donations", "/api/donation-event/list-donation/{eventId}/time-slots/{timeSlotId}/donors", "/api/donation-event/list-donation/{eventId}/donors").hasRole("STAFF")
                        .requestMatchers("/api/donation-event/list-donation/{eventId}/status").hasRole("ADMIN")
                        .requestMatchers("/api/donation-event/**").permitAll()
                        .requestMatchers("/api/medical-facility-stock/**").hasAnyRole("STAFF", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\":\"OAuth2 authentication failed\"}");
                        })
                );


        return http.build();
    }
}