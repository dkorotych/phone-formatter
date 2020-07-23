package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.PhoneFormatterRequest;
import com.github.dkorotych.phone.formatter.domain.PhoneFormatterResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@Controller
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "Phone Formatter",
                version = "1.0"
        )
)
public class PhoneFormatterController {
    @Inject
    private final PhoneFormatterFunction function;

    /**
     * Demo description
     *
     * @param request Request
     * @return Response
     */
    @Post(uri = "/format")
    public PhoneFormatterResponse format(@Body PhoneFormatterRequest request) {
        return function.apply(request);
    }

    @Get(uri = "/format/{phone}")
    public PhoneFormatterResponse format(@PathVariable("phone") String phone) {
        final PhoneFormatterRequest request = new PhoneFormatterRequest();
        request.setPhoneNumber(phone);
        return function.apply(request);
    }
}