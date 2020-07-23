package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.PhoneFormatterRequest;
import com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse;
import com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse.Number.Country;
import com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse.Number.Format;
import com.github.dkorotych.phone.formatter.utils.ErrorBuilder;
import com.github.dkorotych.phone.formatter.utils.Utilities;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;

import javax.inject.Singleton;
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

import static com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse.Error.Code.INVALID_COUNTRY_CODE;
import static com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse.Error.Code.NOT_A_NUMBER;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.*;

@Singleton
public class PhoneFormatterFunction implements Function<PhoneFormatterRequest, PhoneFormatterResponse> {
    private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

    @Override
    public PhoneFormatterResponse apply(PhoneFormatterRequest request) {
        final String phoneNumber = request.getPhoneNumber();
        final String region = request.getCountry();
        final Locale outputLocale = createLocale(region);
        if (StringUtils.hasText(phoneNumber)) {
            final Stream<String> regions;
            if (StringUtils.hasText(region)) {
                if (!PHONE_NUMBER_UTIL.getSupportedRegions().contains(region)) {
                    return createErrorResponse(INVALID_COUNTRY_CODE, outputLocale);
                } else {
                    regions = Stream.of(region);
                }
            } else {
                regions = PHONE_NUMBER_UTIL.getSupportedRegions().stream();
            }
            final PhoneFormatterResponse response = new PhoneFormatterResponse();
            final Map<PhoneNumber, Long> numbers = regions.
                    filter(name -> PHONE_NUMBER_UTIL.isPossibleNumber(phoneNumber, name)).
                    map(name -> {
                        try {
                            final PhoneNumber number = PHONE_NUMBER_UTIL.parse(phoneNumber, name);
                            return PHONE_NUMBER_UTIL.format(number, E164);
                        } catch (NumberParseException exception) {
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
                        final PhoneFormatterResponse.Number currentNumber = new PhoneFormatterResponse.Number();
                        currentNumber.setProbability(probability);
                        final Format format = new Format();
                        format.setE164(PHONE_NUMBER_UTIL.format(number, E164));
                        format.setRfc3966(PHONE_NUMBER_UTIL.format(number, RFC3966));
                        format.setNational(PHONE_NUMBER_UTIL.format(number, NATIONAL));
                        format.setInternational(PHONE_NUMBER_UTIL.format(number, INTERNATIONAL));
                        currentNumber.setFormat(format);
                        currentNumber.setNumber(format.getE164());
                        currentNumber.setValid(PHONE_NUMBER_UTIL.isValidNumber(number));
                        Utilities.createOptionalLocaleForRegion(PHONE_NUMBER_UTIL.getRegionCodeForNumber(number)).
                                ifPresent(locale -> {
                                    final Country country = new Country();
                                    country.setLocale(locale.getCountry());
                                    country.setName(locale.getDisplayCountry(outputLocale));
                                    country.setCode(number.getCountryCode());
                                    currentNumber.setCountry(country);
                                });
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

    private static Locale createLocale(String region) {
        return Utilities.createLocaleForRegion(region);
    }

    private static Predicate<PhoneNumber> createValidationPredicate(boolean onlyValid) {
        return phoneNumber -> !onlyValid || PHONE_NUMBER_UTIL.isValidNumber(phoneNumber);
    }

    private PhoneFormatterResponse createErrorResponse(PhoneFormatterResponse.Error.Code code, Locale outputLocale) {
        final PhoneFormatterResponse response = new PhoneFormatterResponse();
        response.setError(new ErrorBuilder().create(code, outputLocale));
        return response;
    }
}
