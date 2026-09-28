# Metropolis

[![Java CI with Gradle](https://github.com/headhunter45/Metropolis/actions/workflows/gradle.yml/badge.svg)](https://github.com/headhunter45/Metropolis/actions/workflows/gradle.yml)

Metropolis expands a protected Minecraft city as players join. Each player can receive a home plot connected by generated roads; WorldGuard protects the city, homes, and reserved plots.

## Features

- Assigns a protected home plot when a player joins by default and supports acquiring additional homes up to the configured player limit; set `plot.initial` to `0` to require `/metropolis-home-acquire` for the first home.
- Adds stacked plot levels with `plot.maxLevels`, while `plot.sizeY` continues to define the full height of each individual plot.
- Supports per-player plot sizes and home limits through `userOverrides` or configurable permission tiers; acquired homes can be selected as the active home.
- Expands the City region as plots are occupied, with optional spawn generation, world-spawn placement, floors, support blocks, signs, and perimeter walls.
- Generates roads around plots with configurable width, level, material, clearance, and supports.
- Creates named protected reservations from coordinates or a WorldEdit selection so reserved plots are not assigned as homes.
- Lets WorldGuard reservation owners and members teleport to their reservations, with explicit selection when they belong to several.
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

`plugins/Metropolis/config.yml` controls plot dimensions and limits, floor and support generation, signs, road width/level/material/clearance, spawn behavior, wall generation, world name, and per-player overrides. The per-level stack is configured with `plot.sizeY` and `plot.maxLevels`: `sizeY` is the full height of one plot level, and `maxLevels` is the number of stacked levels that can be allocated without exceeding the world build height. `plot.multiplier` sets the default plot-size multiplier; `plot.maxPerPlayer` sets the default home limit; `plot.initial` sets how many homes are auto-generated on join, and `0` requires `/metropolis-home-acquire` for the player's first home.

`plot.offsetX`, `plot.offsetY`, and `plot.offsetZ` shift the logical plot grid in blocks. Positive or negative offsets apply consistently to plot/road placement, vertical levels, spawn alignment, and logical-cell indexing. Changing offsets does not move existing saved WorldGuard regions or automatically convert an existing world.

Before allocating, Metropolis treats every saved WorldGuard region except the enclosing `City` and special `__global__` region as occupied. It uses each region's bounding cuboid; for non-cuboid regions this can conservatively leave extra gaps to ensure new plot regions do not overlap them.

Plot allocation continues outward up to the configured WorldBorder. The complete logical plot-and-road footprint must fit inside the border; if no supported, unoccupied location remains, Metropolis logs an error and creates no region.

Set `spawn.sizeX`, `spawn.sizeY`, and `spawn.sizeZ` to positive logical-grid multipliers. X and Z include the matching plot dimension plus road spacing; Y sets the spawn volume's height upward from `road.level` in multiples of `plot.sizeY`. All default to `1`. A newly created Spawn region must fit within the world's build height, and generation clears only within that region's configured vertical bounds. Existing saved Spawn regions retain their saved bounds.

The `road.streets.*` and `road.avenues.*` sections independently configure width, level, material, clearance, supports, and stairs. Streets run along X; avenues run along Z. Each section's stair settings (`generate`, `material`, `width`, and `everyNBlocks`) use that section's road width and cadence. Street stair runs ascend along positive X at the north/south borders; avenue runs ascend along positive Z at the east/west borders. Runs are centered within each eligible logical plot-sized segment. The lower road remains the normal-material landing; stairs begin one block above and forward from it. Connected following treads use upside-down stairs facing back down the run as backing, and the final tread reaches the upper road Y. The three blocks before each top tread remain open only across the stair width.

Upper-level plots require complete generated support below: every logical plot cell in their footprint must be occupied by a generated home or spawn at every lower level. If a footprint has gaps below, allocation skips that upper candidate and keeps searching for a supported lower-level location.

The WorldGuard `City` region expands in all three dimensions to contain its previous bounds, the requested city footprint, Spawn, and generated homes/reservations. New upper-level plots therefore remain inside City protection; adding a home or reservation also triggers a resize.

The breaking multi-level schema is documented in the example config at `src/main/resources/examples/multi-level.yml`. It uses split road sections (`road.streets.*` and `road.avenues.*`), a level-aware `plot.maxLevels` field, and the valid stair material `COBBLESTONE_STAIRS`.

Roads are generated at their full configured width when a plot is created. Generating an adjacent plot does not rewrite road blocks that are already correct; it only fills missing blocks, including gaps left by older half-width generation. Existing stair treads and upper-road openings are preserved.

Use `permissionOverrides` to assign plot size and home limits through any permission manager. Each permission node maps to a `priority`, `plotMultiplier`, and `maxPlots`; the matching node with the highest priority wins, and the last matching entry in YAML order wins ties. An explicit username entry in `userOverrides` takes precedence over permission rules. If no override matches, the global `plot.*` defaults apply. Permission rules are checked for online players; offline home generation uses username overrides or global defaults. For example:

```yaml
permissionOverrides:
	metropolis.plots.vip:
		priority: 10
		plotMultiplier: 2
		maxPlots: 2
	metropolis.plots.elite:
		priority: 20
		plotMultiplier: 3
		maxPlots: 5
```

Assign these permission nodes to groups or individual users in your permission plugin. Bukkit permissions are boolean, so the configured values determine the numeric limits. The bundled configuration provides the defaults. Metropolis targets Paper 26.2 and 26.3 only; Bukkit and Spigot servers are not supported.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/metropolis` | None | Displays the plugin version. |
| `/metropolis-home-acquire` | Player only | Acquires an available home if the player's home limit is not reached. |
| `/metropolis-home-generate <playerName>` | `metropolis.home.generate` | Generates a home for an online or cached offline player. |
| `/metropolis-home-list` | `metropolis.home.list` | Lists the occupied and reserved Metropolis plots with their bounds. |
| `/metropolis-home-go [playerName]` | `metropolis.home.go` | Teleports the sender, or a permitted target player, to their home. |
| `/metropolis-home-move <homeNumber> [playerName]` | `metropolis.home.move` | Selects one of a player's existing homes as active. The player argument defaults to the sender; console and authorized admins can target online or cached offline players. The selection persists across restarts. |
| `/metropolis-home-evict <playerName>` | `metropolis.home.evict` | Removes the player's ownership from their selected home region. |
| `/metropolis-plot-reserve <name> <minX> <minY> <minZ> <maxX> <maxY> <maxZ>` | `metropolis.plot.reserve` | Creates a named reservation from explicit bounds. A player can instead run `/metropolis-plot-reserve <name>` with a WorldEdit selection. |
| `/metropolis-plot-go [playerName]` | Reservation owner/member; `metropolis.plot.go` to target others or bypass membership | Teleports the sender or an online target to a reservation they own or belong to. If they have several, specify one with `/metropolis-plot-go <reservationName> [playerName]`. |
| `/metropolis-flag-reset` | `metropolis.flag.reset` | Reapplies Metropolis protection flags to the City and home regions. |
| `/metropolis-debug-generatetesthomes <count>` | `metropolis.debug` | Queues test-home generation on the main thread, creating one home every two ticks; intended for development servers. |

## License

GNU General Public License v3.
