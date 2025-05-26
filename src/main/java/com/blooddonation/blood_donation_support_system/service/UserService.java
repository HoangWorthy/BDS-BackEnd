package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.mapper.UserMapper;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

//    public List<UserDto> getAllUsers() {
//        List<User> users = userRepository.findAll();
//        return users.stream().map((employee) -> UserMapper.mapToUserDto(employee))
//                .collect(Collectors.toList());
//    }

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
}
