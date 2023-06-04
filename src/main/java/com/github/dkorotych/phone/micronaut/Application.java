package com.github.dkorotych.phone.micronaut;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Phone Formatter",
                version = "1.0",
                description = """
                        With this API you will be able to get the entered phone number in international,
                         national and several standard formats with additional information about the country for the
                         correct phone numbers. The formatting method can use probabilistic algorithms and try to
                         get all the necessary information if it was not passed in the input parameters
                        """
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}
