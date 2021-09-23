package com.github.dkorotych.phone.formatter.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

public enum ErrorCode {
    @Schema(accessMode = READ_ONLY,
            description = "Number has more digits than any valid phone number could have")
    TOO_LONG_NUMBER,
    @Schema(accessMode = READ_ONLY,
            description = "Country calling code not found or was not recognised")
    INVALID_COUNTRY_CODE,
    @Schema(accessMode = READ_ONLY,
            description = "The input string did not seem to be a phone number")
    NOT_A_NUMBER,
    @Schema(accessMode = READ_ONLY,
            description = "Phone number has an International Direct Dial prefix, but after this prefix was not long " +
                    "enough to be a valid phone number")
    TOO_SHORT_NUMBER_AFTER_IDD,
    @Schema(accessMode = READ_ONLY,
            description = "The entered string is too short to be a national phone number for selected country")
    TOO_SHORT_NUMBER
}
