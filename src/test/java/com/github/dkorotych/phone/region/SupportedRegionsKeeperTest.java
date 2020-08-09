package com.github.dkorotych.phone.region;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dkorotych.phone.region.domain.Region;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import io.micronaut.test.annotation.MicronautTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@MicronautTest
class SupportedRegionsKeeperTest {
    private static Map<String, Region> mapper;
    @Inject
    private SupportedRegionsKeeper keeper;

    @BeforeAll
    static void beforeAll() throws IOException {
        final InputStream stream = FlagsKeeperTest.class.getResourceAsStream("/region/regions.json");
        mapper = new ObjectMapper().readValue(stream, new TypeReference<>() {
        });
    }

    private static Stream<Arguments> get() {
        return PhoneNumberUtil.getInstance().getSupportedRegions().stream().
                map(region -> Arguments.of(region, mapper.get(region)));
    }

    @ParameterizedTest
    @MethodSource
    void get(String region, Region value) {
        Assertions.assertThat(keeper.get(region)).
                isEqualTo(value);
    }

    @Test
    void getMapper() {
        final Map<String, Region> actual = keeper.getMapper();
        final Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();
        Assertions.assertThat(actual).
                hasSize(regions.size()).
                containsOnlyKeys(regions).
                containsExactlyEntriesOf(SupportedRegionsKeeperTest.mapper);
    }
}
