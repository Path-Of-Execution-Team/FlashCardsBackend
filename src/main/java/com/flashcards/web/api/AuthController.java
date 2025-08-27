package com.flashcards.web.api;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.application.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public UserDto createUser(@RequestBody UserCreationDto userCreationDto) {
        return userService.createUser(userCreationDto);
    }
}
