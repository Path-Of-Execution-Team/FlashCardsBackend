package config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class LocaleResolverConfig {

    @Bean
    LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        Locale.Builder builder = new Locale.Builder();
        builder.setLanguage("pl").setRegion("PL");
        Locale polishLocaleBuilder = builder.build();
        localeResolver.setDefaultLocale(polishLocaleBuilder);
        return localeResolver;
    }
}
