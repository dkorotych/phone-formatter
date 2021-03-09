package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.Constant;
import com.github.dkorotych.phone.formatter.domain.Request;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;

@Client("/format")
@Header(name = HttpHeaders.AUTHORIZATION, value = Constant.QA_HEADER)
interface PhoneFormatterControllerClient {
    @Post
    String format(@Body Request request);

    @Get
    String format(@Nullable @QueryValue("phone") String phone);

    @Get
    String format(@Nullable @QueryValue("phone") String phone, @Header(HttpHeaders.ACCEPT_LANGUAGE) String language);
}
