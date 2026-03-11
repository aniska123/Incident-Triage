package com.aniska.incident_triage.service;

import com.aniska.incident_triage.model.User;
import com.aniska.incident_triage.repository.UserRepository;
import com.aniska.incident_triage.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and login.
 *
 * Flow:
 *   Register → validate → encode password → save user
 *   Login    → validate credentials → generate JWT token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    // ─────────────────────────────────────────
    // REGISTER
    // ─────────────────────────────────────────
    @Transactional
    public User register(String username, String email, String password) {
        // Check if username already taken
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken: " + username);
        }

        // Check if email already registered
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        // Build user with encoded password
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))  // bcrypt hash
                .role("ROLE_USER")
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", username);
        return saved;
    }

    // ─────────────────────────────────────────
    // LOGIN — returns JWT token
    // ─────────────────────────────────────────
    public String login(String username, String password) {
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Verify password against bcrypt hash
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate and return JWT token
        String token = jwtUtil.generateToken(username);
        log.info("User '{}' logged in successfully", username);
        return token;
    }

    // ─────────────────────────────────────────
    // GET current user by username
    // ─────────────────────────────────────────
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}