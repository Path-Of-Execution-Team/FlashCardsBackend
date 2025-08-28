package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.domain.exceptions.UnprocessableEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void testValidUserCreationDto_ValidUser() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        UserDto userDto = new UserDto("Puszmen12", "puszmen12@gmail.com");

        var result = userService.createUser(userCreationDto);
        Assertions.assertEquals(userDto, result);
    }

    @Test
    void testValidUserCreationDto_UsernameExists() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        userService.createUser(userCreationDto);
        UserCreationDto conflictUserCreationDto = new UserCreationDto("Puszmen12", "puszmen123@gmail.com", "Sdgdregd123%");
        UnprocessableEntityException exception = Assertions.assertThrows(UnprocessableEntityException.class,
            () -> userService.createUser(conflictUserCreationDto));

        Assertions.assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testValidUserCreationDto_EmailExists() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        userService.createUser(userCreationDto);
        UserCreationDto conflictUserCreationDto = new UserCreationDto("Puszmen123", "puszmen12@gmail.com", "Sdgdregd123%");
        UnprocessableEntityException exception = Assertions.assertThrows(UnprocessableEntityException.class,
            () -> userService.createUser(conflictUserCreationDto));

        Assertions.assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testValidUserCreationDto_WeakPassword() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "qwerty");
        UnprocessableEntityException exception = Assertions.assertThrows(UnprocessableEntityException.class,
            () -> userService.createUser(userCreationDto));

        Assertions.assertEquals("Password should be at least 8 characters, at most 64 characters, " +
            "contains at least one upper and lower case and at least one special character", exception.getMessage());
    }
}
