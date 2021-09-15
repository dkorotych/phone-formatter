package com.github.dkorotych.phone.formatter.utils;

import com.github.dkorotych.phone.formatter.domain.Error;
import com.github.dkorotych.phone.formatter.domain.ErrorCode;
import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.record.Record;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.ObjectConversion;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorBuilderTest {
    private static Map<Locale, Map<String, String>> messages;

    private ErrorBuilder errorBuilder;

    @BeforeAll
    static void beforeAll() {
        messages = new HashMap<>();
        final CsvParser parser = new CsvParser(new CsvParserSettings());
        final InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ErrorBuilderTest.class.getResourceAsStream("/errors.csv")), StandardCharsets.UTF_8);
        final List<Record> records = parser.parseAllRecords(reader);
        for (Record record : records) {
            final Locale locale = record.getValue(0, Locale.ENGLISH, new ObjectConversion<Locale>() {
                @Override
                protected Locale fromString(String s) {
                    return Locale.forLanguageTag(s);
                }
            });
            final String type = record.getString(1);
            final String message = record.getString(2);

            final Map<String, String> map = messages.computeIfAbsent(locale, tmp -> new HashMap<>());
            map.put(type, message);
        }
    }

    @AfterAll
    static void afterAll() {
        messages = null;
    }

    private static Stream<Arguments> create() {
        final Stream.Builder<Arguments> builder = Stream.builder();
        final List<NumberParseException> exceptions = Arrays.asList(
                new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE,
                        "Country calling code supplied was not recognised."),
                new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_AFTER_IDD,
                        "Phone number had an IDD, but after this was not long enough to be a viable phone number."),
                new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER,
                        "The phone number supplied was null."),
                new NumberParseException(NumberParseException.ErrorType.TOO_LONG,
                        "The string supplied was too long to parse."),
                new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER,
                        "The string supplied did not seem to be a phone number."),
                new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE,
                        "Missing or invalid default region."),
                new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE,
                        "Could not interpret numbers after plus-sign."),
                new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN,
                        "The string supplied is too short to be a phone number."),
                new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN,
                        "The string supplied is too short to be a phone number."),
                new NumberParseException(NumberParseException.ErrorType.TOO_LONG,
                        "The string supplied is too long to be a phone number."));
        final List<Locale> locales = Arrays.asList(
                Locale.ENGLISH,
                Locale.CANADA_FRENCH,
                Locale.GERMANY,
                Locale.KOREA,
                new Locale("ru"));
        for (NumberParseException exception : exceptions) {
            for (Locale locale : locales) {
                builder.add(Arguments.of(exception, locale, create(exception.getErrorType(), locale)));
            }
        }
        return builder.build();
    }

    private static Error create(NumberParseException.ErrorType errorType, Locale locale) {
        Map<String, String> map = messages.get(locale);
        if (map == null) {
            map = messages.get(Locale.ENGLISH);
        }
        return new Error(convert(errorType), map.get(errorType.name()));
    }

    private static ErrorCode convert(NumberParseException.ErrorType errorType) {
        return switch (errorType) {
            case TOO_LONG -> ErrorCode.TOO_LONG_NUMBER;
            case INVALID_COUNTRY_CODE -> ErrorCode.INVALID_COUNTRY_CODE;
            case NOT_A_NUMBER -> ErrorCode.NOT_A_NUMBER;
            case TOO_SHORT_AFTER_IDD -> ErrorCode.TOO_SHORT_NUMBER_AFTER_IDD;
            case TOO_SHORT_NSN -> ErrorCode.TOO_SHORT_NUMBER;
        };
    }

    @BeforeEach
    void setUp() {
        errorBuilder = new ErrorBuilder();
    }

    @AfterEach
    void tearDown() {
        errorBuilder = null;
    }

    @ParameterizedTest
    @MethodSource
    void create(NumberParseException exception, Locale locale, Error expected) {
        assertEquals(expected, errorBuilder.create(exception, locale));
    }
}
