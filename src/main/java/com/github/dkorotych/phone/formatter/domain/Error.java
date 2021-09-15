package com.github.dkorotych.phone.formatter.domain;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

@Introspected
@Schema(description = "Information about processing error")
public record Error(
        @Schema(description = "Code of processing error") ErrorCode code,
        @Schema(description = "User friendly error message") String message) {
}
