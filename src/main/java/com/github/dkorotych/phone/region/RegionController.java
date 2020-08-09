package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.region.domain.Region;
import com.github.dkorotych.phone.utils.Utilities;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller("/regions")
@RequiredArgsConstructor
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
public class RegionController {
    @Inject
    private final SupportedRegionsKeeper keeper;

    @Get
    public Collection<Region> getSupportedRegionsWithDefaultLocale(HttpRequest<?> request) {
        return getRegions(Utilities.getLocale(request));
    }

    @Get("/{language}")
    public Collection<Region> getSupportedRegions(@PathVariable(defaultValue = "us") String language) {
        return getRegions(Locale.forLanguageTag(language));
    }

    private Collection<Region> getRegions(Locale locale) {
        return keeper.getSupplier().get().values().stream().
                map(region -> keeper.copyAndAppend(region, locale)).
                collect(Collectors.toList());
    }
}