package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.utils.Utilities;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
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
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@MicronautTest
class RegionControllerTest {
    @Inject
    private RegionControllerClient client;

    private static List<File> getSupportedRegionsWithHeader() {
        return getFilesAsParameters("/region/header");
    }

    private static List<File> getSupportedRegionsWithParameter() {
        return getFilesAsParameters("/region/parameter");
    }

    @ParameterizedTest
    @MethodSource
    void getSupportedRegionsWithHeader(File file) throws Exception {
        validate(file.toPath(), () -> {
            final String language = createLanguage(file);
            return client.getSupportedRegionsWithLocaleFromHeader(language);
        });
    }

    @ParameterizedTest
    @EmptySource
    void getSupportedRegionsWithoutHeader(String language) throws Exception {
        Locale.setDefault(Utilities.DEFAULT_LOCALE);
        validate(getDefaultResponse(), () -> client.getSupportedRegionsWithLocaleFromHeader(language));
    }

    @ParameterizedTest
    @MethodSource
    void getSupportedRegionsWithParameter(File file) throws Exception {
        validate(file.toPath(), () -> {
            final String language = createLanguage(file);
            return client.getSupportedRegions(language);
        });
    }

    private static List<File> getFilesAsParameters(String path) {
        try {
            return Files.list(Paths.get(RegionControllerTest.class.getResource(path).toURI())).
                    map(Path::toFile).
                    filter(File::isFile).
                    filter(file -> file.getName().endsWith(".json")).
                    sorted(Comparator.comparing(File::getName)).
                    collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            Assertions.fail(e);
        }
        throw new RuntimeException();
    }

    private String createLanguage(File file) {
        return file.getName().substring(0, file.getName().indexOf(".json"));
    }

    private void validate(Path expectedPath, Supplier<String> supplier) throws Exception {
        final String expected = Files.readString(expectedPath, StandardCharsets.UTF_8);
        final String actual = supplier.get();
        JSONAssert.assertEquals(expected, actual, true);
    }

    private Path getDefaultResponse() {
        return getSupportedRegionsWithHeader().stream().
                filter(file -> file.getName().equals("us.json")).
                findAny().
                map(File::toPath).
                orElseThrow();
    }
}