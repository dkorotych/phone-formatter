package com.github.dkorotych.phone.formatter.utils;

import com.github.dkorotych.phone.formatter.domain.Error;
import com.github.dkorotych.phone.formatter.domain.ErrorCode;
import com.google.i18n.phonenumbers.NumberParseException;
import io.micronaut.core.util.StringUtils;

import java.util.*;

import static com.github.dkorotych.phone.formatter.domain.ErrorCode.*;
import static com.github.dkorotych.phone.utils.Utilities.DEFAULT_LOCALE;

public class ErrorBuilder {
    public Error create(final NumberParseException e, final Locale locale) {
        return Optional.ofNullable(e).
                map(NumberParseException::getErrorType).
                map(errorType -> switch (errorType) {
                    case TOO_LONG -> TOO_LONG_NUMBER;
                    case INVALID_COUNTRY_CODE -> INVALID_COUNTRY_CODE;
                    case NOT_A_NUMBER -> NOT_A_NUMBER;
                    case TOO_SHORT_AFTER_IDD -> TOO_SHORT_NUMBER_AFTER_IDD;
                    case TOO_SHORT_NSN -> TOO_SHORT_NUMBER;
                }).
                map(code -> create(code, locale)).
                orElse(null);
    }

    public Error create(final ErrorCode code, Locale locale) {
        Locale errorLocale = locale;
        final String language = Optional.ofNullable(locale).
                map(Locale::getLanguage).
                filter(StringUtils::hasText).
                orElse(null);
        if (!StringUtils.hasText(language) && Objects.nonNull(locale)) {
            final String country = Optional.of(locale).
                    map(Locale::getCountry).
                    map(value -> value.toLowerCase(DEFAULT_LOCALE)).
                    orElseThrow();
            if (Arrays.asList(Locale.getISOLanguages()).contains(country)) {
                errorLocale = new Locale(country);
            }
        }
        return new Error(code, getMessage(code, errorLocale));
    }

    private String getMessage(ErrorCode code, Locale locale) {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("errors", locale,
                new ResourceBundle.Control() {
                    @Override
                    public Locale getFallbackLocale(String baseName, Locale locale) {
                        return null;
                    }
                });
        return resourceBundle.getString(code.name());
    }
}
