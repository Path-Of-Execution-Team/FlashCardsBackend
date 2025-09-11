package com.flashcards.web.api;

import com.flashcards.application.dto.LoginUserDto;
import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.application.service.UserService;
import com.flashcards.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("register")
    public UserDto createUser(@RequestBody @Valid UserCreationDto userCreationDto) {
        return userService.createUser(userCreationDto);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.identifier(),
                request.password()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }
}

