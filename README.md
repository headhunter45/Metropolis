# Metropolis

Metropolis expands a protected Minecraft city as players join. Each player can receive a home plot connected by generated roads; WorldGuard protects the city, homes, and reserved plots.

## Requirements

- Paper 26.2, with Java 25 or newer
- WorldGuard 7.0.19
- WorldEdit 7.4.5

WorldGuard and WorldEdit are required plugin dependencies. Install them alongside Metropolis in the server's `plugins` directory.

## Build and Test

Build the plugin and run the server-free unit tests:

```sh
./gradlew clean build
```

The plugin jar is written to `build/libs/`. The Gradle wrapper uses Gradle 9.8, and Git-sensitive semantic versioning derives the artifact and `plugin.yml` versions from repository tags and commits.

Run the opt-in live-server tests against a running Paper server with WorldGuard, WorldEdit, and RCON enabled:

```sh
METROPOLIS_RCON_HOST=127.0.0.1 \
METROPOLIS_RCON_PORT=25575 \
METROPOLIS_RCON_PASSWORD=... \
METROPOLIS_SERVER_DIR=/path/to/server \
./gradlew integrationTest
```

`integrationTest` is separate from the normal `test` and `build` tasks. Keep RCON bound to a trusted interface and do not commit its password.

## Server Scripts

Copy `.env.example` to `.env` and configure the Paper version/build and server paths. The scripts read `.env` as key/value data.

- `tools/build-plugin.sh` builds and tests the plugin.
- `tools/deploy-plugin.sh` copies the Gradle-built jar to the configured server.
- `tools/start-server.sh` downloads the configured Paper build if needed and starts it.
- `tools/backup-server.sh` and `tools/restore-server.sh` back up or restore the configured server.
- `tools/publish-local.sh` publishes the artifact to the local Maven repository.
- `tools/publish-modrinth.sh` uploads a built jar after checking its version.

## Configuration

The default configuration is in `src/main/resources/config.yml` and is copied to `plugins/Metropolis/config.yml` on first startup. Materials use Bukkit names; the previous numeric IDs for stone, grass, cobblestone, and bedrock are still accepted when loading a configuration.

Player home ownership and selected-home data use UUIDs. Existing name-keyed `currentHomes.yml` entries and name-based WorldGuard home regions are recognized during migration.

## Commands

- `/metropolis` displays the plugin version.
- `/metropolis-home-acquire` acquires an available home plot.
- `/metropolis-home-generate <player>` generates a home for a known online or cached offline player.
- `/metropolis-home-list` lists Metropolis plots.
- `/metropolis-home-go [player]` teleports to a home.
- `/metropolis-home-move <home-number> [player]` changes a player's selected home.
- `/metropolis-home-evict <player>` removes a player's home ownership.
- `/metropolis-plot-reserve <name> <minX> <minY> <minZ> <maxX> <maxY> <maxZ>` reserves coordinates; a player may instead provide a WorldEdit selection.
- `/metropolis-plot-go <plot-name> [player]` teleports to a plot.
- `/metropolis-flag-reset` reapplies Metropolis protection flags.

## License

GNU General Public License v3.