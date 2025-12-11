package com.flashcards.application.dto;

import com.flashcards.application.validation.ValidUserCreation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidUserCreation
public record UserCreationDto(@NotBlank(message = "Username cannot be blank")
                              String username,
                              @NotBlank(message = "Email cannot be blank")
                              @Email(message = "Invalid email format")
                              String email,
                              @NotBlank(message = "Password cannot be blank")
                              @NotNull
                              String password) {
}
