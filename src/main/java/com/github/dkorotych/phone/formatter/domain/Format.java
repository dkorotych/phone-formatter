package com.github.dkorotych.phone.formatter.domain;

import lombok.Data;

@Data
public class Format {
    private String e164;
    private String international;
    private String rfc3966;
    private String national;
}
