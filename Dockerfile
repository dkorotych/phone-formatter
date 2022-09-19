FROM gradle:jdk17-alpine as builder
COPY settings.gradle .
COPY build.gradle .
COPY gradle.properties .
COPY src/main ./src/main
RUN gradle --no-daemon buildLayers

FROM openjdk:17-alpine
WORKDIR /home/app
COPY --from=builder /home/gradle/build/docker/main/layers/libs ./libs
COPY --from=builder /home/gradle/build/docker/main/layers/classes ./classes
COPY --from=builder /home/gradle/build/docker/main/layers/resources ./resources
COPY --from=builder /home/gradle/build/docker/main/layers/application.jar ./application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/application.jar"]
