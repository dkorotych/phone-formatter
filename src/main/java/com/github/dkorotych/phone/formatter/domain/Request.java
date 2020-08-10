package com.github.dkorotych.phone.formatter.domain;

import com.github.dkorotych.phone.utils.Utilities;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

@Data
@Introspected
@Schema(description = "Formatting method parameters")
public class Request {
    @Schema(description = "Phone number")
    private String phoneNumber;

    @Schema(nullable = true, description = "Region that we are expecting the number to be from. This is only used if" +
            " the number being parsed is not written in international format")
    private String region;

    @Schema(defaultValue = "true", description = "Display only valid phone numbers. If this parameter is set to false" +
            ", then \"possible\" numbers will be displayed. For example, the correct combination of region code and" +
            " national number, but not the correct combination of mobile operator code")
    private boolean onlyValid = true;

    @Schema(defaultValue = "en", description = "Language in BCP 47 format. Used for output locale sensitive data" +
            " (for example country names)")
    private String language;

    public String getRegion() {
        return Optional.ofNullable(region).
                map(value -> value.toUpperCase(Utilities.DEFAULT_LOCALE)).
                orElse(null);
    }
}
