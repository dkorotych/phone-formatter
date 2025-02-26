package com.github.dkorotych.phone.utils;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
@Hidden
public class PingController {
    @Get("/ping")
    @Status(HttpStatus.OK)
    public void ping() {
    }
}
