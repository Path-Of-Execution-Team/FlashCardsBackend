package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.domain.exceptions.RegistrationException;
import com.flashcards.domain.model.User;
import com.flashcards.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isHealthy() {
        return userRepository.count() >= 0;
    }

    @Transactional
    public UserDto createUser(UserCreationDto userCreationDto) {
        validateUserCreationDto(userCreationDto);
        User user = mapUserCreationDtoToUser(userCreationDto);
        userRepository.save(user);
        return new UserDto(user.getUsername(), user.getEmail());
    }

    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }

    private void validateUserCreationDto(UserCreationDto userCreationDto) {
        if (userRepository.existsByUsername(userCreationDto.username())) {
            throw new RegistrationException("Username already exists", "USER_ALREADY_EXISTS");
        }
        if (userRepository.existsByEmail(userCreationDto.email())) {
            throw new RegistrationException("Email already exists", "EMAIL_ALREADY_TAKEN");
        }
        if (!strongPassword(userCreationDto.passwordHash())) {
            throw new RegistrationException("Password should be at least 8 characters, at most 64 characters," +
                "contains at least one upper and lower case and at least one special character", "WEAK_PASSWORD");
        }
    }

    private User mapUserCreationDtoToUser(UserCreationDto createRequest) {
        User user = new User();
        user.setUsername(createRequest.username());
        user.setEmail(createRequest.email());
        user.setPasswordHash(passwordEncoder.encode(createRequest.passwordHash()));
        return user;
    }

    private boolean strongPassword(String password) {

        String specialChars = "!@#$%^&*()-=_+{};:'|/?.<>,";
        return password.length() >= 8
            && password.length() <= 64
            && password.chars().anyMatch(Character::isDigit)
            && password.chars().anyMatch(Character::isLowerCase)
            && password.chars().anyMatch(Character::isUpperCase)
            && password.chars().anyMatch(c -> specialChars.indexOf(c) != -1);
    }
}
