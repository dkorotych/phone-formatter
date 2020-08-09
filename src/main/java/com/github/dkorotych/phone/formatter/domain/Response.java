package com.github.dkorotych.phone.formatter.domain;

import lombok.Data;

import java.util.Collection;
import java.util.Objects;

@Data
public class Response {
    private Error error;
    private Collection<Number> numbers;

    public boolean isSuccess() {
        return Objects.isNull(error);
    }
}
