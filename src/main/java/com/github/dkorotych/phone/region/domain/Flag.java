package com.github.dkorotych.phone.region.domain;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

@Serdeable
@Schema(description = "Information about country flag")
public record Flag(@Schema(description = "Country flag as a sequence of symbols") String code,
                   @Schema(description = "Country flag as a sequence of HTML codes") String htmlCode) {
}
