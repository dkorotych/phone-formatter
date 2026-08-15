# Phone Formatter

## Build & Run

```sh
./gradlew build                    # тесты + JaCoCo отчёт
./gradlew test                     # только тесты (JaCoCo всё равно выполнится — finalizedBy)
./gradlew dependencyUpdates        # проверить новые версии зависимостей
./gradlew rewriteRun               # применить OpenRewrite рецепты (rewrite.yml)
./gradlew optimizedJitJarAll       # AOT-оптимизированный fat JAR (для Docker/Heroku)
```

- `gradle.properties` — `org.gradle.daemon=false` (каждый запуск свежий демон), `configuration-cache=true` (первый билд медленный, последующие кешированы).
- Docker — `gradle --no-daemon optimizedJitJarAll`, Heroku — `start-on-heroku.sh` (добавляет `--add-opens java.base/java.lang=ALL-UNNAMED`).
- Тулчейн задаёт **JDK 25** (`javaVersion` в `gradle.properties`, `java.toolchain` в `build.gradle.kts`). Gradle возьмёт установленный JDK 25 или скачает его через foojay resolver (`settings.gradle.kts`).

## Тесты

- Все тесты `@MicronautTest` — интеграционные, поднимают приложение.
- Отдельный тест: `./gradlew test --tests 'com.github.dkorotych.phone.formatter.PhoneFormatterControllerTest'` (JaCoCo всё равно выполнится).
- Эндпоинты требуют JWT; тесты используют хардкодный `Constant.QA_TOKEN`. Заголовок — `Constant.QA_HEADER` (`Authorization: Bearer ...`).
- Тестовые данные лежат в `src/test/resources/format/{get,post,both}/{NN}/request.json` + `response.json`. Кейсы `PhoneFormatterControllerTest` подхватываются автоматически из этих директорий — новый кейс = новая папка `{NN}`. Сравнение — `JSONAssert.assertEquals(expected, actual, true)` (strict).
- `application-test.yml` ставит `micronaut.server.port: -1` (случайный порт).
- `TestUserAuthenticationProvider` активируется только в `TEST`-окружении.
- Используются JUnit 5 (`@ParameterizedTest` + `@MethodSource`), AssertJ, JSONAssert.
- Тексты ошибок дублируются: в `src/main/resources/errors{, _ru, _de}.properties` (runtime) и в `src/test/resources/errors.csv` (ожидания). `ErrorBuilderTest` сверяет runtime-сообщения с CSV — меняя текст, обновляй **все** файлы.
- Для доменных тестов (`RegionTest`, `CountryTest`) используется `bean-matchers`.

## Архитектура

- **Контроллеры**: `/format` (POST/GET), `/regions` (GET) — `@Secured(IS_AUTHENTICATED)`, `/ping` (GET) — anonymous, `/openapi/**` — anonymous.
- **Бизнес-логика**: `PhoneFormatterFunction` — `Function<Request, Response>` синглтон.
- **Данные**: регионы вычисляются из `PhoneNumberUtil.getInstance().getSupportedRegions()` — БД нет.
- **OpenAPI**: спецификация генерируется на этапе компиляции (`micronaut-openapi` аннотационный процессор), финальный yml — `build/classes/java/main/META-INF/swagger/phone-formatter-1.0.yml` (его же загружает RapidAPI deploy).
- **Sentry**: настроен на WARN+ через logback, стек ограничен пакетом `com.github.dkorotych.phone`.
- **Пользователи**: `admin`, `qa`, `rapidapi` — настраиваются через `application.yml` (`application.*`), аутентификация по JWT bearer. Пароли переопределяются переменными окружения: `ADMIN_PASSWORD`, `TEST_PASSWORD`, `RAPID_API_PASSWORD`.

## CI/CD

- `Java CI with Gradle` — `./gradlew build` на push/PR в master.
- SonarCloud — `./gradlew build sonar`.
- RapidAPI deploy — загружает `META-INF/swagger/phone-formatter-1.0.yml`.
- CodeQL, Moderne ingest, авто-merge для Dependabot.

## Примечания

- Java 25, Gradle через wrapper, Lombok.
- Зависимости в `gradle/libs.versions.toml` (version catalog).
- Ключевые внешние зависимости: `libphonenumber`, `sentry-logback`, `jackson-serde`.
- JaCoCo добавляется как финализация `test` таска.
