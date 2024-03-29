package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.region.domain.Region;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@MicronautTest
class SupportedRegionsKeeperTest {
    private static Map<String, Region> mapper;
    @Inject
    private SupportedRegionsKeeper keeper;

    @BeforeAll
    static void beforeAll(ObjectMapper objectMapper) throws IOException {
        final InputStream stream = FlagsKeeperTest.class.getResourceAsStream("/region/regions.json");
        mapper = objectMapper.readValue(stream, Argument.mapOf(String.class, Region.class));
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
