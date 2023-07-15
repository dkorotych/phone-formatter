package com.github.dkorotych.phone.region.domain;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Serdeable
@Schema(description = "Information about country")
public class Country {
    @Schema(description = "Country name in specific language")
    private String name;
    private Flag flag;

    public static Country copy(Country from) {
        if (from != null) {
            final Country to = new Country();
            to.setName(from.name);
            to.setFlag(from.flag);
            return to;
        } else {
            return null;
        }
    }
}
