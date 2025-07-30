package com.flashcards.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.annotation.PostConstruct;

@Configuration
public class LocaleResolverConfig {

    @PostConstruct
    public void setDefaultLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Bean
    LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

}
