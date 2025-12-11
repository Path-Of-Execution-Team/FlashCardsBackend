package com.flashcards.application.dto;

import java.util.Objects;

public record UserDto(String username, String email) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(username, userDto.username) && Objects.equals(email, userDto.email);
    }
}

