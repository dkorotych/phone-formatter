package com.github.dkorotych.phone;

import com.github.dkorotych.phone.formatter.domain.Request;
import com.github.dkorotych.phone.formatter.domain.Response;
import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

@MicronautTest
class AuthenticationTest {
    @Client("/format")
    @Inject
    private HttpClient formatClient;

    @Client("/regions")
    @Inject
    private HttpClient regionsClient;

    private static Stream<Arguments> arguments() {
        final Stream.Builder<Arguments> builder = Stream.builder();
        for (String phone : Arrays.asList("+380505095210", "1-800-kodak")) {
            for (HttpMethod method : Arrays.asList(HttpMethod.GET, HttpMethod.POST)) {
                builder.add(Arguments.of(phone, method));
            }
        }
        return builder.build();
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void withToken(String phone, HttpMethod method) {
        final HttpRequest<Response> request = createRequest(method, phone, true);
        final HttpResponse<Response> response = formatClient.toBlocking().exchange(request);
        Assertions.assertThat((Object) response.getStatus()).
                isEqualTo(HttpStatus.OK);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void withoutToken(String phone, HttpMethod method) {
        final HttpRequest<Response> request = createRequest(method, phone, false);
        Assertions.assertThatThrownBy(() -> formatClient.toBlocking().exchange(request)).
                isInstanceOf(HttpClientResponseException.class).
                extracting("status").
                isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpRequest<Response> createRequest(final HttpMethod method, String phone, boolean withAuthorization) {
        final MutableHttpRequest<Response> request = HttpRequest.create(method, UriBuilder.of("").
                queryParam("phone", phone).
                build().
                toString());
        if (HttpMethod.POST.equals(method)) {
            final Request body = new Request();
            body.setPhoneNumber(phone);
            request.body(body);
        }
        if (withAuthorization) {
            request.bearerAuth(Constant.QA_TOKEN);
        }
        return request;
    }
}
