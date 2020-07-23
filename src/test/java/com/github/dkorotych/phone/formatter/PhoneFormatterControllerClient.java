package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.PhoneFormatterRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("/format")
interface PhoneFormatterControllerClient {
    @Post
    String format(@Body PhoneFormatterRequest request);

    @Get("/{phone}")
    String format(@PathVariable("phone") String phone);
}
