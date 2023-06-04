package com.github.dkorotych.phone.formatter.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Collection of formatted phone number")
public record Format(
        @Schema(description = "E164 format is as per international format but with no formatting applied") String e164,
        @Schema(description = """
                International format is consistent with the definition in ITU-T Recommendation E.123 for this region
                """) String international,
        @Schema(description = """
                RFC3966 is as per international format, but with all spaces and other separating symbols replaced with
                 a hyphen, and with any phone number extension appended with ";ext=". It also will have a prefix
                 of "tel:" added, e.g. "tel:+41-44-668-1800".
                """) String rfc3966,
        @Schema(description = """
                National format is consistent with the definition in ITU-T Recommendation E.123 for this region
                """) String national) {
}
