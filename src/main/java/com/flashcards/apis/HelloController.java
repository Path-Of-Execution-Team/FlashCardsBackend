package com.flashcards.apis;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final MessageSource messageSource;

    public HelloController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("/hello")
    public String helloWorld(Locale locale) {
        return messageSource.getMessage("hello.world", null, locale);
    }

    @GetMapping("/")
    public String root(Locale locale) {
        return messageSource.getMessage("root", null, locale);
    }

    @GetMapping("/debug")
    public String debug() {
        System.out.println("LocaleContextHolder.getLocale() = " + LocaleContextHolder.getLocale());
        System.out.println("Default JVM Locale = " + Locale.getDefault());
        return "Check logs";
    }

}
