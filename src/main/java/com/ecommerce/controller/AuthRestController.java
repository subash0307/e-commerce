package com.ecommerce.controller;

import com.ecommerce.dto.UserLoginDto;
import com.ecommerce.dto.UserRegistrationDto;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegistrationDto registrationDto, HttpSession session) {
        User user = userService.register(registrationDto);
        session.setAttribute("currentUser", user);
        return new ResponseEntity<>(Map.of(
                "message", "User registered successfully",
                "userId", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        ), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserLoginDto loginDto, HttpSession session) {
        User user = userService.authenticate(loginDto);
        session.setAttribute("currentUser", user);
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }
}
