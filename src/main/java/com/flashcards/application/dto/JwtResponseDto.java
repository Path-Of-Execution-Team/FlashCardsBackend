package com.flashcards.application.dto;

public record JwtResponseDto(String token) {
    public String getToken() {
        return token;
    }
}
