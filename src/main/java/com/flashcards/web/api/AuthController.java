package com.flashcards.web.api;

import com.flashcards.application.dto.LoginUserDto;
import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreationDto userCreationDto) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userService.createUser(userCreationDto).get());
    }

    @PostMapping("login")
    public CompletableFuture<ResponseEntity<?>> login(@RequestBody LoginUserDto request) throws ExecutionException, InterruptedException {
        return userService.loginUser(request).thenApply(ResponseEntity::ok);
    }
}

