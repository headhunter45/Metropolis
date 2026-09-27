# Contributing

## Development Setup

Use Java 25 or newer. The Gradle wrapper downloads Gradle 9.8; dependencies are declared in `build.gradle` and resolved from Maven Central, PaperMC, and EngineHub.

```sh
./gradlew build
```

The standard build runs JUnit 5 tests without starting Minecraft. Tests use Mockito for Bukkit and external-plugin boundaries. Keep domain and configuration behavior testable without a running server.

## Live Server Tests

Live tests are opt-in and run separately from `build`:

```sh
METROPOLIS_RCON_HOST=127.0.0.1 \
METROPOLIS_RCON_PORT=25575 \
METROPOLIS_RCON_PASSWORD=... \
METROPOLIS_SERVER_DIR=/path/to/server \
./gradlew integrationTest
```

Use a disposable or backed-up Paper server with compatible WorldGuard and WorldEdit plugin jars installed. Keep RCON local, use a temporary password, and restore the server after tests that modify regions or worlds.

## Pull Requests

- Keep changes focused and include tests for behavior changes.
- Run `./gradlew build` before submitting.
- Run `integrationTest` only when changing behavior that requires Paper or the external plugins; do not add it to the default build.
- Update `README.md` and project descriptions when user-facing setup, commands, or dependencies change.
- Keep generated build output, server data, credentials, and local `.env` files out of commits.
