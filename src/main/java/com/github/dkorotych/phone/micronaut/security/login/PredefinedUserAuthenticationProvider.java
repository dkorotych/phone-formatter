package com.github.dkorotych.phone.micronaut.security.login;

import com.github.dkorotych.phone.micronaut.configuration.User;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class PredefinedUserAuthenticationProvider implements AuthenticationProvider {
    private final User user;

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return emitter -> {
            if (Objects.equals(user.getIdentity(), authenticationRequest.getIdentity())
                    && Objects.equals(user.getSecret(), authenticationRequest.getSecret())) {
                emitter.onNext(create());
            } else {
                emitter.onError(AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
            }
        };
    }

    protected AuthenticationResponse create() {
        return AuthenticationResponse.success(user.getIdentity(), user.getRoles());
    }
}
