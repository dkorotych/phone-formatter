package com.github.dkorotych.phone.region.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Information about country flag")
public record Flag(@Schema(description = "Country flag as a sequence of symbols") String code,
                   @Schema(description = "Country flag as a sequence of HTML codes") String htmlCode) {
}
