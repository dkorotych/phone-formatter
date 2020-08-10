package com.github.dkorotych.phone.region.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Information about country flag")
public class Flag {
    @Schema(description = "Country flag as a sequence of symbols")
    private String code;

    @Schema(description = "Country flag as a sequence of HTML codes")
    private String htmlCode;
}
