package com.github.dkorotych.phone.formatter;

import com.github.dkorotych.phone.formatter.domain.Request;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@MicronautTest
class PhoneFormatterControllerTest {

    @Inject
    private PhoneFormatterControllerClient client;

    private static List<Path> getDirectoriesAsParameters(String path) throws URISyntaxException, IOException {
        try (var list = Files.list(Path.of(requireNonNull(PhoneFormatterControllerTest.class.getResource(path)).toURI()))) {
            return list.map(Path::toFile).
                    filter(File::isDirectory).
                    sorted(Comparator.comparing(File::getName)).
                    map(File::toPath).
                    toList();
        }
    }

    private static Stream<Path> get() throws IOException, URISyntaxException {
        return Stream.concat(
                getDirectoriesAsParameters("/format/both").stream(),
                getDirectoriesAsParameters("/format/get").stream()
        );
    }

    private static Stream<Path> post() throws IOException, URISyntaxException {
        return Stream.concat(
                getDirectoriesAsParameters("/format/both").stream(),
                getDirectoriesAsParameters("/format/post").stream()
        );
    }

    @ParameterizedTest
    @MethodSource
    void get(Path path, ObjectMapper objectMapper) throws Exception {
        final Request request = readRequest(path, objectMapper);
        final String expected = readString(path, false);
        final String actual = client.format(request.getPhoneNumber());
        try {
            JSONAssert.assertEquals(expected, actual, true);
        } catch (AssertionError e) {
            throw e;
        }
    }

    @ParameterizedTest
    @MethodSource
    void post(Path path, ObjectMapper objectMapper) throws Exception {
        final Request request = readRequest(path, objectMapper);
        final String expected = readString(path, false);
        final String actual = client.format(request);
        try {
            JSONAssert.assertEquals(expected, actual, true);
        } catch (AssertionError e) {
            throw e;
        }
    }

    private Request readRequest(Path path, ObjectMapper objectMapper) throws IOException {
        final String content = readString(path, true);
        return objectMapper.readValue(content, Request.class);
    }

    private String readString(Path path, boolean request) throws IOException {
        final Path file = path.resolve(request ? "request.json" : "response.json");
        return Files.readString(file, StandardCharsets.UTF_8);
    }
}
