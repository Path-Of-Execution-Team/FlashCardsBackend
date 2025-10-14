package com.flashcards.application.service;

import com.flashcards.application.dto.LoginUserDto;
import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.application.mapper.UserMapper;
import com.flashcards.domain.model.User;
import com.flashcards.infrastructure.persistence.UserRepository;
import com.flashcards.infrastructure.security.JwtService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public boolean isHealthy() {
        return userRepository.count() >= 0;
    }

    @Async
    public CompletableFuture<UserDto> createUser(UserCreationDto userCreationDto) {
        User user = userMapper.toEntity(userCreationDto);
        String passwordHash = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
        return CompletableFuture.completedFuture(new UserDto(user.getUsername(), user.getEmail()));
    }

    @Async
    public CompletableFuture<String> loginUser(LoginUserDto loginUserDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginUserDto.identifier(),
                    loginUserDto.password()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return CompletableFuture.completedFuture(jwtService.generateToken(userDetails));
        } catch (AuthenticationException ex) {
            return CompletableFuture.failedFuture(ex);
        }
    }

    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }
}
