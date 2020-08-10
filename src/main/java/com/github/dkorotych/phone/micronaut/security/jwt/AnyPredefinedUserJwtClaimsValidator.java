package com.github.dkorotych.phone.micronaut.security.jwt;

import com.github.dkorotych.phone.micronaut.configuration.RuntimeConfiguration;
import com.github.dkorotych.phone.micronaut.configuration.User;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.env.Environment;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import io.micronaut.security.token.jwt.validator.GenericJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JWTClaimsSetUtils;

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
    public boolean validate(JwtClaims claims) {
        return validate(JWTClaimsSetUtils.jwtClaimsSetFromClaims(claims));
    }

    private boolean validate(JWTClaimsSet claimsSet) {
        final String subject = claimsSet.getSubject();
        final List<String> roles;
        try {
            roles = Arrays.asList(claimsSet.getStringArrayClaim(rolesName));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        for (User user : users) {
            if (Objects.equals(subject, user.getIdentity()) && Objects.equals(roles, user.getRoles())) {
                return true;
            }
        }
        return false;
    }
}
