package com.github.dkorotych.phone.formatter.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;
import java.util.Objects;

@Data
@Introspected
@Serdeable
@Schema(description = """
        Formatting method execution result. If the execution result contains a set of phone numbers, then the list of
         numbers is sorted in descending order of probabilities, and for equal probabilities by the region code
        """)
public class Response {
    @Schema(nullable = true, description = "Information about the error that occurred")
    private Error error;

    @Schema(nullable = true, description = "List of formatted phone numbers")
    private Collection<Number> numbers;

    @Schema(description = """
            The result of executing the formatting method. If the value is true, then the response must contain a
             non-empty list of formatted phone number values. Otherwise, the response must contain a non-empty object
             with information about the error that occurred
            """)
    public boolean isSuccess() {
        return Objects.isNull(error);
    }
}
