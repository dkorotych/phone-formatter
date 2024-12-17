package com.github.dkorotych.phone.utils;

import com.github.dkorotych.phone.region.SupportedRegionsKeeper;
import com.github.dkorotych.phone.region.domain.Region;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Locale;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
@Hidden
public class PingController {
    @Get("/ping")
    public HttpResponse<?> ping() {
        return HttpResponse.ok();
    }
}
