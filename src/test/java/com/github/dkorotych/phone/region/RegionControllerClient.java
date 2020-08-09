package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.Constant;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Client("/regions")
@Header(name = HttpHeaders.AUTHORIZATION, value = Constant.QA_HEADER)
interface RegionControllerClient {
    @Get
    String getSupportedRegionsWithLocaleFromHeader(@Header(HttpHeaders.ACCEPT_LANGUAGE) String language);

    @Get("/{language}")
    String getSupportedRegions(@PathVariable("language") String language);
}
