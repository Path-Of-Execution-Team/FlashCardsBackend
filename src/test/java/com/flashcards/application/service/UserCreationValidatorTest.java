package com.flashcards.application.service;

import com.flashcards.application.dto.UserCreationDto;
import jakarta.validation.Validator;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class UserCreationValidatorTest {

    @Autowired
    MessageSource messageSource;

    @Autowired
    Validator validator;

    @Autowired
    UserService userService;

    @Test
    public void testIsValid_UsernameExists() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        userService.createUser(userCreationDto);
        UserCreationDto conflictUserCreationDto = new UserCreationDto("Puszmen12", "puszmen123@gmail.com", "Sdgdregd123%");
        Thread.sleep(1000);
        var violations = validator.validate(conflictUserCreationDto);

        String expected = messageSource.getMessage("user.already.exists", null, LocaleContextHolder.getLocale());

        assertThat(violations).anySatisfy(v -> {
            AssertionsForClassTypes.assertThat(v.getPropertyPath().toString()).isEqualTo("username");
            AssertionsForClassTypes.assertThat(v.getMessage()).isEqualTo(expected);
        });
    }

    @Test
    public void testIsValid_EmailExists() throws InterruptedException {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "Sdgdregd123%");
        userService.createUser(userCreationDto);
        UserCreationDto conflictUserCreationDto = new UserCreationDto("Puszmen123", "puszmen12@gmail.com", "Sdgdregd123%");
        Thread.sleep(1000);
        var violations = validator.validate(conflictUserCreationDto);

        String expected = messageSource.getMessage("email.already.taken", null, LocaleContextHolder.getLocale());

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
            assertThat(v.getMessage()).isEqualTo(expected);
        });
    }

    @Test
    void testIsValid_WeakPassword() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", "qwerty");
        var violations = validator.validate(userCreationDto);

        String expected = messageSource.getMessage("weak.password", null, LocaleContextHolder.getLocale());
        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
            assertThat(v.getMessage()).isEqualTo(expected);
        });
    }

    @Test
    void testIsValid_NullUsername() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto(null, "puszmen12@gmail.com", "Sdgdregd123%");
        var violations = validator.validate(userCreationDto);

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("username");
        });
    }

    @Test
    void testIsValid_NullEmail() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", null, "Sdgdregd123%");
        var violations = validator.validate(userCreationDto);

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("email");
        });
    }

    @Test
    void testIsValid_NullPassword() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Puszmen12", "puszmen12@gmail.com", null);
        var violations = validator.validate(userCreationDto);

        assertThat(violations).anySatisfy(v -> {
            assertThat(v.getPropertyPath().toString()).isEqualTo("password");
        });
    }
}
