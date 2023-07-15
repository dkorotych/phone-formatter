package com.github.dkorotych.phone.formatter.domain;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Serdeable
@Schema(description = "Information about processing error")
public record Error(
        @Schema(description = "Code of processing error") ErrorCode code,
        @Schema(description = "User friendly error message") String message) {
}
