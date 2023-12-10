FROM gradle:jdk21-alpine AS builder
WORKDIR /home/app
COPY settings.gradle .
COPY build.gradle .
COPY gradle.properties .
COPY src/main ./src/main
RUN gradle --no-daemon optimizedJitJarAll

FROM bellsoft/liberica-openjre-alpine:21
WORKDIR /home/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/phone-formatter-0.1-all-optimized.jar"]
