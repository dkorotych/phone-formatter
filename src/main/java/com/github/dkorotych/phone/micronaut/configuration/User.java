package com.github.dkorotych.phone.micronaut.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;

@Data
public class User {
    @NotBlank
    private String identity;
    @NotBlank
    private String secret;
    private Collection<String> roles = Collections.emptySet();
}
