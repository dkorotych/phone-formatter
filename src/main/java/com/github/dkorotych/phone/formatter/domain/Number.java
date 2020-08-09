package com.github.dkorotych.phone.formatter.domain;

import com.github.dkorotych.phone.region.domain.Region;
import lombok.Data;

import java.util.Comparator;
import java.util.Optional;

@Data
public class Number implements Comparable<Number> {
    private String number;
    private boolean valid;
    private double probability;
    private Format format;
    private Region region;

    @Override
    public int compareTo(Number o) {
        return Comparator.comparing(Number::isValid).
                thenComparingDouble(Number::getProbability).
                reversed().
                thenComparingInt(number -> Optional.ofNullable(number.getRegion()).
                        map(Region::getCode).
                        orElse(Integer.MIN_VALUE)).
                thenComparing(Number::getNumber).
                compare(this, o);
    }

}
