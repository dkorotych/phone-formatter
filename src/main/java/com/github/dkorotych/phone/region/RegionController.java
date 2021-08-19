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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Support methods that allow you to get options for advanced formatting of phone numbers
 */
@Controller("/regions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
@RequiredArgsConstructor
@Tag(name = "Support")
public class RegionController {
    @Inject
    private final SupportedRegionsKeeper keeper;

    /**
     * Returns a collection of supported regions. Method use "{@code Accept-Language}" header value for output list of
     * regions on specific language
     *
     * @param request Http request
     * @return Collection of supported regions
     */
    @Get
    public Collection<Region> simpleRegions(@Parameter(hidden = true) HttpRequest<?> request) {
        return getRegions(Utilities.getLocale(request));
    }

    /**
     * Returns a collection of supported regions for the specified
     * <a href="https://en.wikipedia.org/wiki/IETF_language_tag">IETF BCP 47</a> language tag string.
     *
     * @param language Language in BCP 47 format
     * @return Collection of supported regions
     */
    @Get("/{language}")
    public Collection<Region> regions(@PathVariable(defaultValue = "us") String language) {
        return getRegions(Locale.forLanguageTag(language));
    }

    private Collection<Region> getRegions(Locale locale) {
        return keeper.getSupplier().get().values().stream().
                map(region -> keeper.copyAndAppend(region, locale)).
                collect(Collectors.toList());
    }
}