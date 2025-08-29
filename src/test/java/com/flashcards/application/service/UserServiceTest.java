package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.application.dto.UserDto;
import com.flashcards.domain.exceptions.UnprocessableEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

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

        String expectedMessage = messageSource.getMessage("user.already.exists", null, LocaleContextHolder.getLocale());

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testValidUserCreationDto_EmailExists() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        userService.createUser(userCreationDto);
        UserCreationDto conflictUserCreationDto = new UserCreationDto("Puszmen123", "puszmen12@gmail.com", "Sdgdregd123%");
        UnprocessableEntityException exception = Assertions.assertThrows(UnprocessableEntityException.class,
            () -> userService.createUser(conflictUserCreationDto));

        String expectedMessage = messageSource.getMessage("email.already.taken", null, LocaleContextHolder.getLocale());
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testValidUserCreationDto_WeakPassword() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "qwerty");
        UnprocessableEntityException exception = Assertions.assertThrows(UnprocessableEntityException.class,
            () -> userService.createUser(userCreationDto));

        String expectedMessage = messageSource.getMessage("weak.password", null, LocaleContextHolder.getLocale());
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
