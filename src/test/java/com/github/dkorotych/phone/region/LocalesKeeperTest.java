package com.github.dkorotych.phone.region;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MicronautTest
class LocalesKeeperTest {
    private static Map<String, Locale> mapper;
    @Inject
    private LocalesKeeper keeper;

    @BeforeAll
    static void beforeAll(ObjectMapper objectMapper) throws IOException {
        final InputStream stream = LocalesKeeperTest.class.getResourceAsStream("/region/locales.json");
        mapper = objectMapper.readValue(stream, Argument.mapOf(String.class, String.class)).entrySet().stream().
                map(entry -> {
                    final String key = entry.getKey();
                    final String value = entry.getValue();
                    Locale locale = Locale.forLanguageTag(value.replace('_', '-'));
                    if (locale == Locale.ROOT) {
                        locale = Locale.of(Locale.ROOT.getLanguage(), value.split("_")[1]);
                    }
                    return Map.entry(key, locale);
                }).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Stream<Arguments> get() {
        return PhoneNumberUtil.getInstance().getSupportedRegions().stream().
                map(region -> Arguments.of(region, mapper.get(region)));
    }

    @ParameterizedTest
    @MethodSource
    void get(String region, Locale value) {
        Assertions.assertThat(keeper.get(region)).
                isEqualTo(value);
    }

    @Test
    void getMapper() {
        final Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();
        Assertions.assertThat(keeper.getMapper()).
                hasSize(regions.size()).
                containsOnlyKeys(regions).
                containsExactlyEntriesOf(mapper);
    }
}
