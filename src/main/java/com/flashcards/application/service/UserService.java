package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.application.mapper.UserMapper;
import com.flashcards.domain.model.User;
import com.flashcards.infrastructure.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public boolean isHealthy() {
        return userRepository.count() >= 0;
    }

    @Transactional
    public UserDto createUser(@Valid UserCreationDto userCreationDto) {
        User user = userMapper.toEntity(userCreationDto);
        String passwordHash = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
        return new UserDto(user.getUsername(), user.getEmail());
    }

    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }
}
