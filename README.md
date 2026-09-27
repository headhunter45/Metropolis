# Metropolis

Metropolis expands a protected Minecraft city as players join. Each player can receive a home plot connected by generated roads; WorldGuard protects the city, homes, and reserved plots.

## Features

- Assigns a protected home plot when a player joins and supports acquiring additional homes up to the configured player limit.
- Expands the City region as plots are occupied, with optional spawn generation, world-spawn placement, floors, support blocks, signs, and perimeter walls.
- Generates roads around plots with configurable width, level, material, clearance, and supports.
- Creates named protected reservations from coordinates or a WorldEdit selection so reserved plots are not assigned as homes.
- Persists selected homes by player UUID and recognizes legacy name-based home data during migration.
- Supports online/offline player administration for home generation, movement, and eviction.

## Requirements

- Paper 26.2 or 26.3, with Java 25 or newer
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
- `tools/publish-modrinth.sh` uploads a built jar after checking its version and generates a Markdown changelog from Git commits since the previous version tag. Set `MODRINTH_CHANGELOG` to override it with curated notes.


## Configuration

`plugins/Metropolis/config.yml` controls plot dimensions and limits, floor and support generation, signs, road width/level/material/clearance, spawn behavior, wall generation, world name, and per-player overrides. The bundled configuration provides the defaults. Metropolis targets Paper 26.2 and 26.3 only; Bukkit and Spigot servers are not supported.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/metropolis` | None | Displays the plugin version. |
| `/metropolis-home-acquire` | Player only | Acquires an available home if the player's home limit is not reached. |
| `/metropolis-home-generate <playerName>` | `metropolis.home.generate` | Generates a home for an online or cached offline player. |
| `/metropolis-home-list` | `metropolis.home.list` | Lists the occupied and reserved Metropolis plots with their bounds. |
| `/metropolis-home-go [playerName]` | `metropolis.home.go` | Teleports the sender, or a permitted target player, to their home. |
| `/metropolis-home-move <homeNumber> [playerName]` | `metropolis.home.move` | Selects one of a player's existing home regions. The player argument defaults to the sender. |
| `/metropolis-home-evict <playerName>` | `metropolis.home.evict` | Removes the player's ownership from their selected home region. |
| `/metropolis-plot-reserve <name> <minX> <minY> <minZ> <maxX> <maxY> <maxZ>` | `metropolis.plot.reserve` | Creates a named reservation from explicit bounds. A player can instead run `/metropolis-plot-reserve <name>` with a WorldEdit selection. |
| `/metropolis-plot-go <plotName> [playerName]` | `metropolis.plot.go` | Teleports the sender or target player to a named occupied or reserved plot. |
| `/metropolis-flag-reset` | `metropolis.flag.reset` | Reapplies Metropolis protection flags to the City and home regions. |
| `/metropolis-debug-generatetesthomes <count>` | `metropolis.debug` | Generates test homes; intended for development servers. |

## License

GNU General Public License v3.
