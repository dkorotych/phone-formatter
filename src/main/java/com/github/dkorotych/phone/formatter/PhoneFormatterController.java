package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.Request;
import com.github.dkorotych.phone.formatter.domain.Response;
import com.github.dkorotych.phone.utils.Utilities;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@Controller("/format")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
@Tag(name = "Format")
@RequiredArgsConstructor
public class PhoneFormatterController {
    @Inject
    private final PhoneFormatterFunction function;

    /**
     * Formatting method that outputs the entered phone number in international, national and several standard formats
     * with additional country information for correct phone numbers
     *
     * @param request Request parameters
     * @return Response Formatting method execution result
     */
    @Post
    public Response format(@Valid @Body Request request) {
        return function.apply(request);
    }

    /**
     * A simplified version of the formatting method that does not accept additional filtering and parsing parameters
     * and uses the language value from the "{@code Accept-Language}" header.
     *
     * @param phone       Phone number
     * @param httpRequest Http request
     * @return Response Formatting method execution result
     */
    @Get
    public Response simple(@Nullable @QueryValue("phone") String phone,
                           @Parameter(hidden = true) HttpRequest<?> httpRequest) {
        final Request request = new Request();
        request.setPhoneNumber(phone);
        request.setLanguage(getLanguage(httpRequest));
        return function.apply(request);
    }

    private String getLanguage(HttpRequest<?> httpRequest) {
        return Utilities.getLocale(httpRequest).getLanguage();
    }
}
