package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.Request;
import com.github.dkorotych.phone.formatter.domain.Response;
import com.github.dkorotych.phone.utils.Utilities;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller("/format")
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "Phone Formatter",
                version = "1.0"
        )
)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Validated
public class PhoneFormatterController {
    @Inject
    private final PhoneFormatterFunction function;

    /**
     * Demo description
     *
     * @param request Request
     * @return Response
     */
    @Post
    public Response format(@Valid @Body Request request) {
        return function.apply(request);
    }

    /**
     * This method is equivalent to {@link #format(Request request)} with {@code request}
     * {@code
     * {
     * "phoneNumber": phone,
     * "language": headers[ACCEPT_LANGUAGE]
     * }
     * }.
     *
     * @param phone       Phone number
     * @param httpRequest Http request
     * @return Response
     * @see #format(Request)
     */
    @Get
    public Response simpleFormat(@QueryValue("phone") String phone,
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