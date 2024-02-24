package com.github.dkorotych.phone.micronaut.security.login;

import com.github.dkorotych.phone.micronaut.configuration.User;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class PredefinedUserAuthenticationProvider implements HttpRequestAuthenticationProvider<Object> {
    private final User user;

    public AuthenticationResponse authenticate(@Nullable HttpRequest<Object> httpRequest,
                                               @NonNull AuthenticationRequest<String, String> authenticationRequest) {
        if (Objects.equals(user.getIdentity(), authenticationRequest.getIdentity())
                && Objects.equals(user.getSecret(), authenticationRequest.getSecret())) {
            return AuthenticationResponse.success(user.getIdentity(), user.getRoles());
        } else {
            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }
    }
}
