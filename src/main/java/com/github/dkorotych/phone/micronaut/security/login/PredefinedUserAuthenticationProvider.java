package com.github.dkorotych.phone.micronaut.security.login;

import com.github.dkorotych.phone.micronaut.configuration.User;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class PredefinedUserAuthenticationProvider implements AuthenticationProvider {
    private final User user;

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flowable.create(emitter -> {
            if (Objects.equals(user.getIdentity(), authenticationRequest.getIdentity())
                    && Objects.equals(user.getSecret(), authenticationRequest.getSecret())) {
                emitter.onNext(create(authenticationRequest));
                emitter.onComplete();
            } else {
                final AuthenticationException exception = new AuthenticationException(new AuthenticationFailed(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
                emitter.onError(exception);
            }
        }, BackpressureStrategy.ERROR);
    }

    protected UserDetails create(AuthenticationRequest<?, ?> request) {
        return new UserDetails(user.getIdentity(), user.getRoles());
    }
}
