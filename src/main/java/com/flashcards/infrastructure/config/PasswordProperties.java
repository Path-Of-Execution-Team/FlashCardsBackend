package com.flashcards.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.password")
public record PasswordProperties(
    int minLength,
    int maxLength
) {
}
