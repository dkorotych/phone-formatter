package com.github.dkorotych.phone.formatter.utils;

import io.micronaut.core.util.StringUtils;
import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class Utilities {
    public Locale createLocaleForRegion(String name) {
        return createOptionalLocaleForRegion(name).
                orElse(Locale.ENGLISH);
    }

    public Optional<Locale> createOptionalLocaleForRegion(String name) {
        return Optional.ofNullable(name).
                filter(StringUtils::hasText).
                map(region -> new Locale.Builder().
                        setRegion(region).
                        build());
    }
}
