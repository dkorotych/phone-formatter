package com.github.dkorotych.phone.formatter.domain;

import lombok.Data;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

@Data
public class PhoneFormatterResponse {
    private Error error;
    private Collection<Number> numbers;

    public boolean isSuccess() {
        return Objects.isNull(error);
    }

    @Data
    public static class Number implements Comparable<Number> {
        private String number;
        private boolean valid;
        private double probability;
        private Format format;
        private Country country;

        @Override
        public int compareTo(Number o) {
            return Comparator.comparing(Number::isValid).
                    thenComparingDouble(Number::getProbability).
                    reversed().
                    thenComparingInt(number -> Optional.ofNullable(number.getCountry()).
                            map(Country::getCode).
                            orElse(Integer.MIN_VALUE)).
                    thenComparing(Number::getNumber).
                    compare(this, o);
        }

        @Data
        public static class Format {
            private String e164;
            private String international;
            private String rfc3966;
            private String national;
        }

        @Data
        public static class Country {
            private String name;
            private String locale;
            private int code;
        }
    }

    @Data
    public static class Error {
        private final Code code;
        private final String message;

        public enum Code {
            TOO_LONG_NUMBER,
            INVALID_COUNTRY_CODE,
            NOT_A_NUMBER,
            TOO_SHORT_NUMBER_AFTER_IDD,
            TOO_SHORT_NUMBER,
            UNKNOWN
        }
    }
}
