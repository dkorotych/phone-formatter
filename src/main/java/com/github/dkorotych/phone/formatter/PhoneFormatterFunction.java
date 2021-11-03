package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.Number;
import com.github.dkorotych.phone.formatter.domain.*;
import com.github.dkorotych.phone.formatter.utils.ErrorBuilder;
import com.github.dkorotych.phone.region.LocalesKeeper;
import com.github.dkorotych.phone.region.SupportedRegionsKeeper;
import com.github.dkorotych.phone.utils.Utilities;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.dkorotych.phone.formatter.domain.ErrorCode.INVALID_COUNTRY_CODE;
import static com.github.dkorotych.phone.formatter.domain.ErrorCode.NOT_A_NUMBER;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.*;

@Singleton
@Slf4j
public class PhoneFormatterFunction implements Function<Request, Response> {
    private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
    @Inject
    private SupportedRegionsKeeper keeper;
    @Inject
    private LocalesKeeper localesKeeper;

    @Override
    public Response apply(Request request) {
        final String phoneNumber = request.getPhoneNumber();
        final String region = request.getRegion();
        final Locale outputLocale = createLocale(request);
        final Stream<String> regions = createRegions(region);
        if (regions == null) {
            return createErrorResponse(INVALID_COUNTRY_CODE, outputLocale);
        }
        if (StringUtils.hasText(phoneNumber)) {
            final Response response = new Response();
            final Map<PhoneNumber, Long> numbers = regions.
                    filter(name -> PHONE_NUMBER_UTIL.isPossibleNumber(phoneNumber, name)).
                    map(name -> {
                        try {
                            final PhoneNumber number = PHONE_NUMBER_UTIL.parse(phoneNumber, name);
                            return PHONE_NUMBER_UTIL.format(number, E164);
                        } catch (NumberParseException exception) {
                            log.warn("Incorrect phone number", exception);
                            if (StringUtils.hasText(region)) {
                                response.setError(new ErrorBuilder().create(exception, outputLocale));
                                return null;
                            }
                        }
                        return null;
                    }).
                    filter(Objects::nonNull).
                    map(number -> {
                        try {
                            return PHONE_NUMBER_UTIL.parse(number, null);
                        } catch (NumberParseException ignored) {
                        }
                        return null;
                    }).
                    filter(Objects::nonNull).
                    filter(createValidationPredicate(request.isOnlyValid())).
                    collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            final long sum = numbers.values().stream().
                    mapToLong(value -> value).
                    sum();
            response.setNumbers(numbers.entrySet().stream().
                    map(entry -> {
                        final double probability = BigDecimal.valueOf(entry.getValue()).
                                divide(BigDecimal.valueOf(sum), 3, RoundingMode.HALF_UP).
                                doubleValue();
                        final PhoneNumber number = entry.getKey();
                        final Number currentNumber = new Number();
                        currentNumber.setProbability(probability);
                        final Format format = new Format();
                        format.setE164(PHONE_NUMBER_UTIL.format(number, E164));
                        format.setRfc3966(PHONE_NUMBER_UTIL.format(number, RFC3966));
                        format.setNational(PHONE_NUMBER_UTIL.format(number, NATIONAL));
                        format.setInternational(PHONE_NUMBER_UTIL.format(number, INTERNATIONAL));
                        currentNumber.setFormat(format);
                        currentNumber.setNumber(format.getE164());
                        currentNumber.setValid(PHONE_NUMBER_UTIL.isValidNumber(number));
                        currentNumber.setRegion(keeper.get(PHONE_NUMBER_UTIL.getRegionCodeForNumber(number), outputLocale));
                        return currentNumber;
                    }).
                    sorted().
                    collect(Collectors.toCollection(LinkedHashSet::new)));
            if (CollectionUtils.isEmpty(response.getNumbers())) {
                response.setError(new ErrorBuilder().create(NOT_A_NUMBER, outputLocale));
            }
            return response;
        } else {
            return createErrorResponse(NOT_A_NUMBER, outputLocale);
        }
    }

    private Stream<String> createRegions(final String region) {
        final Stream<String> regions;
        if (StringUtils.hasText(region)) {
            if (!PHONE_NUMBER_UTIL.getSupportedRegions().contains(region)) {
                regions = null;
            } else {
                regions = Stream.of(region);
            }
        } else {
            regions = PHONE_NUMBER_UTIL.getSupportedRegions().stream();
        }
        return regions;
    }

    private Locale createLocale(Request request) {
        if (StringUtils.hasText(request.getLanguage())) {
            return Locale.forLanguageTag(request.getLanguage());
        }
        final Locale locale = localesKeeper.get(request.getRegion());
        if (Objects.isNull(locale)) {
            return Utilities.DEFAULT_LOCALE;
        }
        return locale;
    }

    private static Predicate<PhoneNumber> createValidationPredicate(boolean onlyValid) {
        return phoneNumber -> !onlyValid || PHONE_NUMBER_UTIL.isValidNumber(phoneNumber);
    }

    private Response createErrorResponse(ErrorCode code, Locale outputLocale) {
        final Response response = new Response();
        response.setError(new ErrorBuilder().create(code, outputLocale));
        return response;
    }
}
