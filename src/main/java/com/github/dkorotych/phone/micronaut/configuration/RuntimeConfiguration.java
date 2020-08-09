package com.github.dkorotych.phone.micronaut.configuration;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;

@ConfigurationProperties("application")
@Getter
public class RuntimeConfiguration {
    @ConfigurationBuilder(configurationPrefix = "admin")
    private final User admin = new User();

    @ConfigurationBuilder(configurationPrefix = "test")
    private final User test = new User();
}
