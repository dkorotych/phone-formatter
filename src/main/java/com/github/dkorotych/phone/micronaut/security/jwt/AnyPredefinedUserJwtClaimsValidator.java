package com.github.dkorotych.phone.micronaut.security.jwt;

import com.github.dkorotych.phone.micronaut.configuration.RuntimeConfiguration;
import com.github.dkorotych.phone.micronaut.configuration.User;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import io.micronaut.security.token.jwt.validator.GenericJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JWTClaimsSetUtils;
import io.sentry.Sentry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.ParseException;
import java.util.*;

@Singleton
public class AnyPredefinedUserJwtClaimsValidator implements GenericJwtClaimsValidator {
    private final Collection<User> users;
    private final String rolesName;

    @Inject
    public AnyPredefinedUserJwtClaimsValidator(RuntimeConfiguration configuration, Environment environment,
                                               TokenConfiguration tokenConfiguration) {
        users = new ArrayList<>();
        users.add(configuration.getAdmin());
        users.add(configuration.getRapidApi());
        if (environment.getActiveNames().contains(Environment.TEST)) {
            users.add(configuration.getTest());
        }
        rolesName = tokenConfiguration.getRolesName();
    }

    @Override
    @Deprecated
    public boolean validate(JwtClaims claims) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validate(JwtClaims claims, HttpRequest<?> request) {
        return validate(JWTClaimsSetUtils.jwtClaimsSetFromClaims(claims));
    }

    private boolean validate(JWTClaimsSet claimsSet) {
        final String subject = claimsSet.getSubject();
        final List<String> roles;
        try {
            roles = Arrays.asList(claimsSet.getStringArrayClaim(rolesName));
        } catch (ParseException e) {
            throw new AuthenticationException(e.getMessage());
        }
        for (User user : users) {
            if (Objects.equals(subject, user.getIdentity()) && Objects.equals(roles, user.getRoles())) {
                final HashMap<String, String> data = new HashMap<>();
                data.put(rolesName, String.join(", ", user.getRoles()));
                final io.sentry.protocol.User sentryUser = new io.sentry.protocol.User();
                sentryUser.setUsername(user.getIdentity());
                sentryUser.setOthers(data);
                Sentry.setUser(sentryUser);
                return true;
            }
        }
        return false;
    }
}
