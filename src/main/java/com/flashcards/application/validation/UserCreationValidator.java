package com.flashcards.application.validation;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.infrastructure.persistence.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UserCreationValidator implements ConstraintValidator<ValidUserCreation, UserCreationDto> {

    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public UserCreationValidator(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public boolean isValid(UserCreationDto userCreationDto, ConstraintValidatorContext ctx) {
        if (userCreationDto == null) {
            return false;
        }
        Locale locale = LocaleContextHolder.getLocale();
        boolean ok = true;
        if (userRepository.existsByUsername(userCreationDto.username())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(messageSource.getMessage("username.already.exists", null, locale))
                .addPropertyNode("username").addConstraintViolation();
            ok = false;
        }
        if (userRepository.existsByEmail(userCreationDto.email())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(messageSource.getMessage("email.already.taken", null, locale))
                .addPropertyNode("email").addConstraintViolation();
            ok = false;
        }
        if (!strongPassword(userCreationDto.password())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(messageSource.getMessage("weak.password", null, locale))
                .addPropertyNode("password").addConstraintViolation();
            ok = false;
        }
        return ok;
    }

    private boolean strongPassword(String password) {

        if (password == null) return false;
        String specialChars = "!@#$%^&*()-=_+{};:'|/?.<>,";
        return password.length() >= 8
            && password.length() <= 64
            && password.chars().anyMatch(Character::isDigit)
            && password.chars().anyMatch(Character::isLowerCase)
            && password.chars().anyMatch(Character::isUpperCase)
            && password.chars().anyMatch(c -> specialChars.indexOf(c) != -1);
    }
}
