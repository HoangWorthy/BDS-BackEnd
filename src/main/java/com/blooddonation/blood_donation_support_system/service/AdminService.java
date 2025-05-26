package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.dto.UserDto;
import com.blooddonation.blood_donation_support_system.entity.Role;
import com.blooddonation.blood_donation_support_system.entity.User;
import com.blooddonation.blood_donation_support_system.mapper.UserMapper;
import com.blooddonation.blood_donation_support_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return UserMapper.mapToUserDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUserRole(String userEmail, String newRole){
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + userEmail);
        } else {
            user.setRole(Role.valueOf(newRole));
            User updatedUserEntity = userRepository.save(user);
            return UserMapper.mapToUserDto(updatedUserEntity);
        }
    }


}
