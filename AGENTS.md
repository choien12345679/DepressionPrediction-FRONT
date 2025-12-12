# Repository Guidelines

## Project Structure & Modules
- `app/`: Android launcher, dependency wiring (Hilt), config and packaging. Uses `BuildConfig` keys such as `KAKAO_API_KEY`.
- `presentation/`: UI layer with View/DataBinding, navigation, widgets, and ViewModels under `presentation/src/main/kotlin/com/imp/presentation`; resources in `presentation/src/main/res`, static assets in `presentation/src/main/assets`.
- `domain/`: Pure Kotlin models, repository interfaces, and use cases; keep it Android-free for testability.
- `data/`: Remote sources, mappers, persistence, and tracking; implements `domain` repositories. BuildConfig exposes server hosts and Kakao REST keys.
- Shared Gradle settings live in `gradle/` and `gradle.properties`. Keep secrets in `local.properties` (ignored by VCS).

## Build, Test, and Development Commands
- `./gradlew clean build` — full build across all modules.
- `./gradlew :app:assembleDebug` — produce a debug APK; `./gradlew :app:installDebug` pushes it to a connected device/emulator.
- `./gradlew lintDebug` — Android lint report at `app/build/reports/lint/lint.html`.
- `./gradlew :app:testDebugUnitTest` — JVM unit tests.
- `./gradlew :app:connectedDebugAndroidTest` — instrumentation tests on an emulator/device.

## Coding Style & Naming Conventions
- Kotlin, Java 17, 4-space indent; run Android Studio reformat before committing.
- Package by layer (`com.imp.presentation`, `...data`, `...domain`); keep new use cases/models in `domain`, repository implementations in `data`, UI/ViewModels in `presentation`.
- Classes/objects use PascalCase; functions camelCase; resource files snake_case (e.g., `activity_home.xml`); string IDs `feature_action_label`.
- Use Hilt for DI; expose runtime config via `BuildConfig` instead of hard-coded constants.
- Prefer coroutines where available; keep RxJava usage consistent within a feature to avoid mixing styles mid-flow.

## Testing Guidelines
- JUnit for unit tests; AndroidX runner/espresso for UI flows when needed.
- Place tests beside modules (`presentation/src/test`, `data/src/test`, etc.); name files `FooRepositoryTest`.
- Cover new use cases, mappers, and ViewModels; add screenshot or instrumentation coverage when changing screens.
- Run lint and unit tests before PRs; for device-dependent changes also run `connectedDebugAndroidTest`.

## Environment & Secrets
- Required in `local.properties`: `kakao_api_key`, `kakao_rest_api_key`, `service_server_host`, `dev_server_host`, `chat_server_host`, `chatting_server_host`. Keep values out of commits and PR text.
- Do not bundle credentials in assets or resources; read them via `BuildConfig`.

## Commit & Pull Request Guidelines
- Follow existing pattern: include ticket and type when available (e.g., `FP-187 feat: adjust analysis UI`); keep messages concise.
- Branch/PR descriptions should note scope, testing performed, and any migrations.
- For UI changes, attach before/after screenshots; link the relevant issue/bug ID.
- Keep PRs module-focused (UI in `presentation`, data changes in `data`, etc.) and mention any new Gradle dependencies or config flags.
