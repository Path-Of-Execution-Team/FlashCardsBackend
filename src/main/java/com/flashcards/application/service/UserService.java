package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.domain.exceptions.UnprocessableEntityException;
import com.flashcards.domain.model.User;
import com.flashcards.infrastructure.persistence.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
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
        Locale locale = LocaleContextHolder.getLocale();
        if (userRepository.existsByUsername(userCreationDto.username())) {
            throw new UnprocessableEntityException(
                messageSource.getMessage("user.already.exists", null, locale),
                "USER_ALREADY_EXISTS");
        }
        if (userRepository.existsByEmail(userCreationDto.email())) {
            throw new UnprocessableEntityException(
                messageSource.getMessage("email.already.taken", null, locale),
                "EMAIL_ALREADY_TAKEN");
        }
        if (!strongPassword(userCreationDto.passwordHash())) {
            throw new UnprocessableEntityException(
                messageSource.getMessage("weak.password", null, locale),
                "WEAK_PASSWORD");
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
