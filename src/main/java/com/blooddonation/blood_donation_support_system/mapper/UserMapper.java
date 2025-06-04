package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setBloodType(user.getBloodType());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setRole(user.getRole());
        dto.setLastDonationDate(user.getLastDonationDate());
        dto.setNextEligibleDonationDate(user.getNextEligibleDonationDate());
        dto.setPersonalId(user.getPersonalId());

        return dto;
    }

    public static User mapToUser(UserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setBloodType(dto.getBloodType());
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRole(dto.getRole());
        user.setLastDonationDate(dto.getLastDonationDate());
        user.setNextEligibleDonationDate(dto.getNextEligibleDonationDate());
        user.setPersonalId(dto.getPersonalId());

        return user;
    }
}