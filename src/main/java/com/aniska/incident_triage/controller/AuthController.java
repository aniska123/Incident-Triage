package com.aniska.incident_triage.controller;

import com.aniska.incident_triage.dto.AuthRequestDTO;
import com.aniska.incident_triage.dto.AuthResponseDTO;
import com.aniska.incident_triage.dto.RegisterRequestDTO;
import com.aniska.incident_triage.model.User;
import com.aniska.incident_triage.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        User user = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponseDTO.builder()
                        .username(user.getUsername())
                        .role(user.getRole())
                        .message("User registered successfully")
                        .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody AuthRequestDTO request) {
        String token = authService.login(
                request.getUsername(),
                request.getPassword());
        User user = authService.getUserByUsername(request.getUsername());
        return ResponseEntity.ok(
                AuthResponseDTO.builder()
                        .token(token)
                        .username(user.getUsername())
                        .role(user.getRole())
                        .message("Login successful")
                        .build());
    }
}
