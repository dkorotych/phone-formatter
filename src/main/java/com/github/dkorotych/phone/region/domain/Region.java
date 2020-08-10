package com.github.dkorotych.phone.region.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Information about region")
public class Region {
    @Schema(description = "Region name (code)")
    private String name;

    @Schema(description = "Region calling code")
    private int code;

    @Schema(nullable = true, description = "Information about country (if exists country for this region)")
    private Country country;

    public static Region copy(Region from) {
        if (from != null) {
            final Region to = new Region();
            to.setCode(from.code);
            to.setName(from.name);
            to.setCountry(Country.copy(from.country));
            return to;
        } else {
            return null;
        }
    }
}
