package com.github.dkorotych.phone.formatter.domain;

import com.github.dkorotych.phone.region.domain.Region;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Comparator;
import java.util.Optional;

@Data
@Serdeable
@Schema(description = "Information about phone number")
public class Number implements Comparable<Number> {
    @SuppressWarnings("java:S1700")
    @Schema(description = "Phone number formatted in E164 format")
    private String number;

    @Schema(description = """
            The phone number can be "valid" (correct) and "possible". If this flag is set to true, then this number is
             correct for this region. If the value is false, then this is a "possible" number and it may contain, for
             example, an incorrect mobile operator code, although all other groups of numbers will be correct
            """)
    private boolean valid;

    @Schema(description = """
            The coefficient of the probability of the correctness of the parsing of the telephone number. For example,
             you specified a phone number in the national format, which contains the correct set of numbers for several
             regions, but did not specify a specific region of the number. In this case, the algorithm will return all
             the required numbers and regions, but will try to calculate the probability that a specific combination
             of number and region is the required data
            """)
    private double probability;

    @Schema(description = "Collection of formatted phone number")
    private Format format;

    @Schema(description = "Information about region")
    private Region region;

    @Override
    public int compareTo(Number o) {
        return Comparator.comparing(Number::isValid).
                thenComparingDouble(Number::getProbability).
                reversed().
                thenComparingInt(phoneNumber -> Optional.ofNullable(phoneNumber.getRegion()).
                        map(Region::getCode).
                        orElse(Integer.MIN_VALUE)).
                thenComparing(Number::getNumber).
                compare(this, o);
    }
}
