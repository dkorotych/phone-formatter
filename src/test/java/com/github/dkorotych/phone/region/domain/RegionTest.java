package com.github.dkorotych.phone.region.domain;

import com.github.dkorotych.phone.region.FlagsKeeper;
import com.github.dkorotych.phone.region.LocalesKeeper;
import com.github.dkorotych.phone.region.SupportedRegionsKeeper;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static com.google.code.beanmatchers.BeanMatchers.*;
import static org.hamcrest.Matchers.allOf;

class RegionTest {
    private static final SupportedRegionsKeeper SUPPORTED_REGIONS_KEEPER = new SupportedRegionsKeeper(new FlagsKeeper(), new LocalesKeeper());
    private static final String[] REGIONS = new String[]{
            "RU", "AI", "SA", "TN", "TO"
    };

    public static Stream<Region> copy() {
        final Stream.Builder<Region> builder = Stream.builder();
        builder.add(new Region());
        for (String region : REGIONS) {
            builder.add(SUPPORTED_REGIONS_KEEPER.get(region));
        }
        return builder.build();
    }

    @Test
    void bean() {
        MatcherAssert.assertThat(Region.class, allOf(
                hasValidBeanConstructor(),
                hasValidGettersAndSetters(),
                hasValidBeanHashCode(),
                hasValidBeanEquals(),
                hasValidBeanToString()
        ));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource
    void copy(Region region) {
        Assertions.assertThat(Region.copy(region)).isEqualTo(region);
    }
}
