package com.pulserise.pulserise_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// ✅ Use jakarta.validation for Spring Boot 3.x
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    // Optional fitness-related fields
    private String goal; // e.g., "weight_loss", "muscle_gain", "maintenance"
    private String gender; // "male", "female", "other"
    private Integer age;
    private Double weight; // in kg
    private Double height; // in cm
    private String activityLevel; // "sedentary", "lightly_active", "moderately_active", "very_active"

    // Method to check if passwords match
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}