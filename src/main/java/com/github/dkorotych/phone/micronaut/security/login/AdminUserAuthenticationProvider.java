package com.github.dkorotych.phone.micronaut.security.login;

import com.github.dkorotych.phone.micronaut.configuration.RuntimeConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AdminUserAuthenticationProvider extends PredefinedUserAuthenticationProvider {
    @Inject
    public AdminUserAuthenticationProvider(RuntimeConfiguration configuration) {
        super(configuration.getAdmin());
    }
}
