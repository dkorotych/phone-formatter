package com.github.dkorotych.phone.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dkorotych.phone.formatter.domain.Request;
import io.micronaut.http.client.HttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MicronautTest
class PhoneFormatterControllerTest {

    @Inject
    private PhoneFormatterControllerClient client;
    @Inject
    HttpClient httpClient;
    private ObjectMapper objectMapper;

    private static List<Path> getDirectoriesAsParameters(String path) {
        try {
            return Files.list(Paths.get(PhoneFormatterControllerTest.class.getResource(path).toURI())).
                    map(Path::toFile).
                    filter(File::isDirectory).
                    sorted(Comparator.comparing(File::getName)).
                    map(File::toPath).
                    collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            Assertions.fail(e);
        }
        throw new RuntimeException();
    }

    private static Stream<Path> get() {
        return Stream.concat(
                getDirectoriesAsParameters("/format/both").stream(),
                getDirectoriesAsParameters("/format/get").stream()
        );
    }

    private static Stream<Path> post() {
        return Stream.concat(
                getDirectoriesAsParameters("/format/both").stream(),
                getDirectoriesAsParameters("/format/post").stream()
        );
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        objectMapper = null;
    }

    @ParameterizedTest
    @MethodSource
    void get(Path path) throws Exception {
        final Request request = readRequest(path);
        final String expected = readString(path, false);
        final String actual = client.format(request.getPhoneNumber());
        try {
            JSONAssert.assertEquals(expected, actual, true);
        } catch (Throwable e) {
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource
    void post(Path path) throws Exception {
        final Request request = readRequest(path);
        final String expected = readString(path, false);
        final String actual = client.format(request);
        try {
            JSONAssert.assertEquals(expected, actual, true);
        } catch (Throwable e) {
            throw e;
        }
    }

    private Request readRequest(Path path) throws IOException {
        final String content = readString(path, true);
        return objectMapper.readValue(content, Request.class);
    }

    private String readString(Path path, boolean request) throws IOException {
        final Path file = path.resolve(request ? "request.json" : "response.json");
        return Files.readString(file, StandardCharsets.UTF_8);
    }
}
