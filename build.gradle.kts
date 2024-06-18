import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.0"
    id("io.micronaut.aot") version "4.4.0"
    id("org.openrewrite.rewrite") version "6.16.0"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.sonarqube") version "5.0.0.4638"
}

version = "0.1"
group = "com.github.dkorotych.phone.formatter"
val javaVersion = project.extra["javaVersion"]
val micronautVersion = project.extra["micronautVersion"]

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.yaml:snakeyaml")

    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.39")
    implementation("io.sentry:sentry-logback:7.10.0")

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("org.assertj:assertj-core:3.26.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")

    rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:2.12.0"))
    rewrite("org.openrewrite.recipe:rewrite-github-actions")
    rewrite("org.openrewrite.recipe:rewrite-logging-frameworks")
    rewrite("org.openrewrite.recipe:rewrite-static-analysis")
    rewrite("org.openrewrite.recipe:rewrite-java-security")
    rewrite("org.openrewrite.recipe:rewrite-testing-frameworks")
    rewrite("org.openrewrite.recipe:rewrite-migrate-java")
    rewrite("org.openrewrite.recipe:rewrite-micronaut")
    rewrite("org.openrewrite:rewrite-gradle")

    aotPlugins(platform("io.micronaut.platform:micronaut-platform:${micronautVersion}"))
    aotPlugins("io.micronaut.security:micronaut-security-aot")
}


application {
    mainClass.set("com.github.dkorotych.phone.micronaut.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion("$javaVersion")
    targetCompatibility = JavaVersion.toVersion("$javaVersion")
}

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.github.dkorotych.phone.formatter.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
        configurationProperties.put("micronaut.security.jwks.enabled", "false")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "$javaVersion"
}

rewrite {
    activeRecipe("com.github.dkorotych.phone.formatter.CustomRecipes")
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    revision = "release"
    gradleReleaseChannel = "current"
    rejectVersionIf {
        val version = candidate.version
        version.contains("alpha")
                || version.contains("beta")
                || ".+-M\\d+$".toRegex().matches(version)
                || ".+-RC\\d+$".toRegex().matches(version)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "dkorotych_phone-formatter")
        property("sonar.organization", "dkorotych")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
