package com.github.dkorotych.phone.utils;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class Utilities {
    public static final Locale DEFAULT_LOCALE = Locale.US;
    private static final List<String> LANGUAGES = Arrays.asList(Locale.getISOLanguages());

    public Optional<Locale> createOptionalLocaleForRegion(String name) {
        return Optional.ofNullable(name).
                filter(StringUtils::hasText).
                map(region -> {
                    try {
                        final Locale.Builder builder = new Locale.Builder();
                        final String language = region.toLowerCase(DEFAULT_LOCALE);
                        if (LANGUAGES.contains(language)) {
                            builder.setLanguage(language);
                        }
                        builder.setRegion(region);
                        return builder.build();
                    } catch (Exception e) {
                        return null;
                    }
                });
    }

    public Locale getLocale(HttpRequest<?> request) {
        return request.getLocale().
                map(Utilities::getSupportedLocale).
                orElse(DEFAULT_LOCALE);
    }

    public Locale getSupportedLocale(Locale locale) {
        return Optional.ofNullable(locale).
                filter(item -> {
                    try {
                        item.getISO3Language();
                        return true;
                    } catch (MissingResourceException ignored) {
                        return false;
                    }
                }).
                orElse(DEFAULT_LOCALE);
    }
}
