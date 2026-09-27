**Metropolis**

Metropolis grows a protected Minecraft city as players join. It assigns home plots connected by roads, expands the protected city region, and lets players and administrators manage homes and reserve plots.

## Requirements

- Paper 26.2 or 26.3.
- Java 25 or newer.
- WorldGuard 7.0.19 and WorldEdit 7.4.5.

WorldGuard and WorldEdit are required dependencies. Install all three plugins in the server's `plugins` directory.

Metropolis targets Paper only; Bukkit and Spigot servers are not supported.

## Features

- Automatically assigns a protected home plot when a player joins.
- Allows players to acquire additional homes up to their effective limit, configured globally, per user, or through permission-manager groups.
- Supports permission-based plot-size and home-limit tiers with configurable priorities; explicit per-user overrides take precedence.
- Lets players and authorized administrators select an existing home as active; selections persist by player UUID across restarts.
- Expands the protected city region as plots are occupied.
- Configures plot dimensions, roads, floors, supports, signs, spawn behavior, and perimeter walls.
- Reserves named plots by coordinates or a WorldEdit selection.
- Tracks home ownership and selected homes by player UUID.
- Supports home and plot teleportation, administration, and WorldGuard protection flags.

## Configuration

Metropolis creates `plugins/Metropolis/config.yml` on first startup. Configure plot limits and dimensions, roads, floors and supports, signs, spawn behavior, walls, world name, and per-player overrides. Use Bukkit material names; legacy numeric material IDs are also accepted for existing configurations.

## Commands

- `/metropolis` - Displays the plugin version.
- `/metropolis-home-acquire` - Acquires another available home plot.
- `/metropolis-home-generate <playerName>` - Generates a home for a known online or cached offline player.
- `/metropolis-home-list` - Lists occupied and reserved plots.
- `/metropolis-home-go [playerName]` - Teleports to a home.
- `/metropolis-home-move <homeNumber> [playerName]` - Selects an existing home as active. The player name defaults to the sender; authorized administrators and console can target online or cached offline players.
- `/metropolis-home-evict <playerName>` - Removes a player's ownership from their selected home region.
- `/metropolis-plot-reserve <name> <minX> <minY> <minZ> <maxX> <maxY> <maxZ>` - Reserves a region by coordinates; players can use `/metropolis-plot-reserve <name>` with a WorldEdit selection.
- `/metropolis-plot-go <plotName> [playerName]` - Teleports to a named plot.
- `/metropolis-flag-reset` - Reapplies Metropolis protection flags.
- `/metropolis-debug-generatetesthomes <count>` - Generates test homes; intended for development servers.

## Build

Run `./gradlew build` from the project directory. The plugin jar is created in `build/libs/`.
