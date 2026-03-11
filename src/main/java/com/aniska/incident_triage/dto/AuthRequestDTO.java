package com.aniska.incident_triage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login request.
 *
 * POST /auth/login
 * {
 *   "username": "john",
 *   "password": "secret123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
