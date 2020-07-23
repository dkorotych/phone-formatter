package com.github.dkorotych.phone.formatter.utils;

import com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse;
import com.google.i18n.phonenumbers.NumberParseException;

import java.util.Locale;
import java.util.Optional;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse.Error.Code.*;

public class ErrorBuilder {
    public PhoneFormatterResponse.Error create(final NumberParseException e, final Locale locale) {
        return Optional.ofNullable(e).
                map(NumberParseException::getErrorType).
                map(errorType -> {
                    switch (errorType) {
                        case TOO_LONG:
                            return TOO_LONG_NUMBER;
                        case INVALID_COUNTRY_CODE:
                            return INVALID_COUNTRY_CODE;
                        case NOT_A_NUMBER:
                            return NOT_A_NUMBER;
                        case TOO_SHORT_AFTER_IDD:
                            return TOO_SHORT_NUMBER_AFTER_IDD;
                        case TOO_SHORT_NSN:
                            return TOO_SHORT_NUMBER;
                        default:
                            return UNKNOWN;
                    }
                }).
                map(code -> create(code, locale)).
                orElse(null);
    }

    public PhoneFormatterResponse.Error create(final PhoneFormatterResponse.Error.Code code, final Locale locale) {
        return new PhoneFormatterResponse.Error(code, getMessage(code, locale));
    }

    private String getMessage(PhoneFormatterResponse.Error.Code code, Locale locale) {
        final ResourceBundle resourceBundle = PropertyResourceBundle.getBundle("errors", locale,
                new ResourceBundle.Control() {
                    @Override
                    public Locale getFallbackLocale(String baseName, Locale locale) {
                        return null;
                    }
                });
        return resourceBundle.getString(code.name());
    }
}
