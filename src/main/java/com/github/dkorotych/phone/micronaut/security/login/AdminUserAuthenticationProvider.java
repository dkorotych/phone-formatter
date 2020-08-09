package com.github.dkorotych.phone.micronaut.security.login;

import com.github.dkorotych.phone.micronaut.configuration.RuntimeConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AdminUserAuthenticationProvider extends PredefinedUserAuthenticationProvider {
    @Inject
    public AdminUserAuthenticationProvider(RuntimeConfiguration configuration) {
        super(configuration.getAdmin());
    }
}
