package com.ecommerce.service;

import com.ecommerce.dto.UserLoginDto;
import com.ecommerce.dto.UserRegistrationDto;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService with SHA-256 password hashing.
 *
 * Architectural Note (Interview Prep):
 * - @Service registers this class as a Spring Bean in the service tier.
 * - @Transactional ensures database consistency across method calls.
 * - Password hashing secures user credentials against database breaches.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User register(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("An account with email " + registrationDto.getEmail() + " already exists.");
        }

        User user = User.builder()
                .fullName(registrationDto.getFullName())
                .email(registrationDto.getEmail().trim().toLowerCase())
                .password(hashPassword(registrationDto.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .phone(registrationDto.getPhone())
                .address(registrationDto.getAddress())
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticate(UserLoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!user.getPassword().equals(hashPassword(loginDto.getPassword()))) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
