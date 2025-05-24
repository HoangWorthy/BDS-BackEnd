package com.blooddonation.blood_donation_support_system.mapper;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
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
    }

    public static User mapToUser(UserDto userDto) {
        return new User(
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
    }
}