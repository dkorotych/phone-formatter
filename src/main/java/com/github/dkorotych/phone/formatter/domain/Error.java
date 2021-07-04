package com.github.dkorotych.phone.formatter.domain;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Introspected
@Schema(description = "Information about processing error")
public class Error {
    @Schema(description = "Code of processing error")
    private final ErrorCode code;

    @Schema(description = "User friendly error message")
    private final String message;
}
