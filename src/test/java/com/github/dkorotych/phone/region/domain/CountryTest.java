package com.github.dkorotych.phone.region.domain;

import com.github.dkorotych.phone.region.FlagsKeeper;
import com.google.code.beanmatchers.BeanMatchers;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static com.google.code.beanmatchers.BeanMatchers.*;
import static org.hamcrest.Matchers.allOf;

class CountryTest {
    private static final FlagsKeeper FLAGS_KEEPER = new FlagsKeeper();
    private static final String[] REGIONS = new String[]{
            "RU", "AI", "SA", "TN", "TO"
    };

    public static Stream<Country> copy() {
        final Stream.Builder<Country> builder = Stream.builder();
        builder.add(new Country());
        for (String region : REGIONS) {
            final Country country = new Country();
            country.setName(region);
            country.setFlag(FLAGS_KEEPER.get(region));
            builder.add(country);
        }
        return builder.build();
    }

    @Test
     void bean() {
        BeanMatchers.registerValueGenerator(() -> {
            final String region = REGIONS[ThreadLocalRandom.current().nextInt(REGIONS.length)];
            return FLAGS_KEEPER.get(region);
        }, Flag.class);
        MatcherAssert.assertThat(Country.class, allOf(
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
    void copy(Country country) {
        Assertions.assertThat(Country.copy(country)).isEqualTo(country);
    }
}
