package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UserMapper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getPhone(),
                user.getAddress(),
                user.getBloodType(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getRole(),
                user.getLastDonationDate(),
                user.getPersonalId()
        );
        return dto;
    }

    public static User mapToUser(UserDto userDto) {
        User user = new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getName(),
                userDto.getPhone(),
                userDto.getAddress(),
                userDto.getBloodType(),
                userDto.getGender(),
                userDto.getDateOfBirth(),
                userDto.getRole(),
                userDto.getLastDonationDate(),
                userDto.getPersonalId()
        );
        return user;
    }
}