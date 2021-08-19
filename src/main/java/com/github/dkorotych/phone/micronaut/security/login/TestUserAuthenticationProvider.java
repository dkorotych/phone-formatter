package com.github.dkorotych.phone.micronaut.security.login;

import com.github.dkorotych.phone.micronaut.configuration.RuntimeConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Requires(env = Environment.TEST)
public class TestUserAuthenticationProvider extends PredefinedUserAuthenticationProvider {

    @Inject
    public TestUserAuthenticationProvider(RuntimeConfiguration configuration) {
        super(configuration.getTest());
    }
}
