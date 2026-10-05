package com.ecommerce.service;

import com.ecommerce.dto.UserLoginDto;
import com.ecommerce.dto.UserRegistrationDto;
import com.ecommerce.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(UserRegistrationDto registrationDto);
    User authenticate(UserLoginDto loginDto);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> getAllUsers();
    long countUsers();
}
