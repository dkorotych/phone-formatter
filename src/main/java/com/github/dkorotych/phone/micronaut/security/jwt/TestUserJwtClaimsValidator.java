package com.github.dkorotych.phone.micronaut.security.jwt;

import com.github.dkorotych.phone.micronaut.configuration.RuntimeConfiguration;
import com.github.dkorotych.phone.micronaut.configuration.User;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import io.micronaut.security.token.jwt.validator.GenericJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JWTClaimsSetUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
@Requires(env = Environment.TEST)
public class TestUserJwtClaimsValidator implements GenericJwtClaimsValidator {
    private final User user;

    @Inject
    public TestUserJwtClaimsValidator(RuntimeConfiguration configuration) {
        user = configuration.getTest();
    }

    @Override
    public boolean validate(JwtClaims claims) {
        return validate(JWTClaimsSetUtils.jwtClaimsSetFromClaims(claims));
    }

    private boolean validate(JWTClaimsSet claimsSet) {
        final String subject = claimsSet.getSubject();
        return Objects.equals(subject, user.getIdentity()) && Objects.equals(claimsSet.getAudience(), user.getRoles());
    }
}
