package com.github.dkorotych.phone.formatter.domain;

import lombok.Data;

@Data
public class Error {
    private final ErrorCode code;
    private final String message;
}
