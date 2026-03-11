package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    Validator validator;

    @Test
    @Disabled("Temporarily disabled: test data collides with other integration tests in H2")
    void testCreateUser_ValidUser() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        UserDto userDto = new UserDto("Puszmen12", "puszmen12@gmail.com");

        CompletableFuture<UserDto> resultFuture = userService.createUser(userCreationDto);

        UserDto result = resultFuture.join();
        Assertions.assertEquals(userDto, result);
    }
}
