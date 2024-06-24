FROM gradle:jdk21-alpine AS builder
WORKDIR /home/app
COPY settings.gradle.kts .
COPY gradle/libs.versions.toml ./gradle/
COPY build.gradle.kts .
COPY gradle.properties .
COPY src/main ./src/main
RUN gradle --no-daemon optimizedJitJarAll

FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /home/app
COPY --from=builder /home/app/build/libs/phone-formatter-0.1-all-optimized.jar ./application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/application.jar"]
