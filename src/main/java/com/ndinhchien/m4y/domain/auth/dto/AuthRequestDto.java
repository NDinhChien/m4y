package com.ndinhchien.m4y.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

public class AuthRequestDto {

    @Getter
    public static class RegisterRequestDto {

        @Schema(example = "test")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must be alphanumeric only")
        private String userName;

        @Schema(example = "test@email.com")
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        private String email;

        @Schema(example = "Aa12345$")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$", message = "Password must be at least 6 characters long and include uppercase, lowercase, number, and special character (!@#$%^&*)")
        private String password;

        @Schema(example = "Aa12345$")
        @NotBlank(message = "Please confirm your password")
        private String confirmPassword;
    }

    @Getter
    public static class LoginRequestDto {

        @Schema(example = "test@email.com")
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        private String email;

        @Schema(example = "Aa12345$")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$", message = "Password must be at least 6 characters long and include uppercase, lowercase, number, and special character (!@#$%^&*)")
        private String password;

    }

    @Getter
    public static class UpdatePasswordDto {

        @NotBlank
        private String oldPassword;

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$", message = "Password must be at least 6 characters long and include uppercase, lowercase, number, and special character (!@#$%^&*)")
        private String newPassword;

        @NotBlank
        private String confirmPassword;

    }

    @Getter
    public static class VerifyAccountDto {
        @NotBlank
        private String token;
    }

    @Getter
    public static class ResetPasswordDto {

        @NotBlank
        private String token;

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$", message = "Password must be at least 6 characters long and include uppercase, lowercase, number, and special character (!@#$%^&*)")
        private String password;

        @NotBlank
        private String confirmPassword;
    }

}
