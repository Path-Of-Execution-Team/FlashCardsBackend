package com.flashcards.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreationDto(@NotBlank(message = "Username cannot be blank")
                              String username,
                              @NotBlank(message = "Email cannot be blank")
                              @Email(message = "Invalid email format")
                              String email,
                              @NotBlank(message = "Password cannot be blank")
                              @NotNull
                              String password) {
}
