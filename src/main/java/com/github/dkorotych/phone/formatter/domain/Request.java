package com.github.dkorotych.phone.formatter.domain;

import com.github.dkorotych.phone.utils.Utilities;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import java.util.Optional;

@Data
@Introspected
public class Request {
    /**
     * Phone number
     */
    private String phoneNumber;
    private String region;
    /**
     * Show only valid phones. If this parameter set to {@code false}
     */
    private boolean onlyValid = true;
    private String language;

    public String getRegion() {
        return Optional.ofNullable(region).
                map(value -> value.toUpperCase(Utilities.DEFAULT_LOCALE)).
                orElse(null);
    }
}
