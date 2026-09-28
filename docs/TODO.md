# Modernize Metropolis Plugin: Implementation Tasks

|   ID    |  Status  | Title |
|:-------:|:--------:|:------|
| MET-001 |   Done   | Initialize Gradle in the project root, add the Gradle wrapper, and create the project build file. |
| MET-002 |   Done   | Set Gradle project metadata and configure a Java toolchain compatible with the target modern Paper version. |
| MET-003 |   Done   | Configure repositories and compatible PaperMC, WorldEdit, WorldGuard, and other required or optional dependencies; add build plugins such as Shadow only if needed. |
| MET-004 |   Done   | Move or verify plugin.yml under src/main/resources, configure Gradle resource processing, and update its Paper api-version, dependencies, commands, permissions, and required fields. |
| MET-005 |   Done   | Add JUnit 5 test infrastructure and keep the default Gradle `test` task server-free. Use an in-memory Paper harness such as MockBukkit for Bukkit/Paper behavior and mocks or test doubles for WorldEdit, WorldGuard, and narrow API boundaries where appropriate. |
| MET-006 |   Done   | Build the initial Gradle project and run its tests; resolve build setup issues before removing the existing Maven build. |
| MET-007 |   Done   | Update .gitignore for Gradle and remove obsolete Maven and Eclipse project files after the Gradle build succeeds. |
| MET-008 |   Done   | Analyze Java sources for deprecated or removed Bukkit/Paper APIs and legacy patterns, including logging, event registration, configuration, player lookup, and player-keyed storage. |
| MET-009 |   Done   | Review custom scripts and resources for required updates to the Gradle build and modern Paper server workflow. |
| MET-010 |   Done   | Refactor plugin logging to use JavaPlugin.getLogger(). |
| MET-011 |   Done   | Refactor event listeners and registration to use Listener, @EventHandler, and the modern plugin manager API. |
| MET-012 |   Done   | Replace deprecated configuration APIs with the modern Bukkit/Paper configuration API, including load, save, and reload behavior. |
| MET-013 |   Done   | Replace Player-keyed storage with UUID-based storage wherever player identity is persisted or tracked. |
| MET-014 |   Done   | Review player lookup logic and use exact or otherwise explicit name-matching behavior where appropriate. |
| MET-015 |   Done   | Refactor remaining code that uses deprecated or removed Bukkit, Spigot, or Paper APIs so it is compatible with the target Paper API. |
| MET-016 |   Done   | Add fast unit tests for plot and home allocation, reservation validation, player lookup and UUID-based ownership, configuration persistence, command validation, and event behavior. Keep domain rules independent of a running Minecraft server and mock external plugin boundaries as needed. |
| MET-017 |   Done   | Add a separate Gradle `integrationTest` source set/task for tests requiring an actual Paper server and compatible WorldGuard/WorldEdit plugins. Verify plugin loading, command registration and dispatch, region-backed reservations and home allocation, and persistence across a server restart. Run it only when explicitly requested; keep it out of the normal `test` and `build` tasks. |
| MET-018 |   Done   | Build the plugin with Gradle and use the provided server scripts to smoke-test it on modern Paper with its required plugins installed. |
| MET-019 |   Done   | Address bugs and incompatibilities found by unit tests, integration tests, or modern Paper server testing. |
| MET-020 |   Done   | Adopt the git-sensitive semantic versioning Gradle plugin used by MobScores and ScoreKeeper, using the existing 0.5-SNAPSHOT version as the migration baseline; verify version/tag behavior and enable the Gradle configuration cache when compatible. |
| MET-021 |   Done   | Update README.md, CONTRIBUTING.md, other documentation, and project scripts with the Gradle build, test, versioning, and modern Paper server workflow. |
| MET-022 | Planning | (Optional) Add new features, quality-of-life improvements, or CI configuration after the modernization and documentation tasks are complete. |
| MET-023 | Planning | Add an optional home-allocation policy that grants all remaining homes up to a player's limit when they join. |
| MET-024 | Planning | Add an optional home-allocation policy that grants at most one additional home per login until the player's limit is reached. |
| MET-025 | Planning | Add a configuration option to choose the plot-allocation traversal algorithm: concentric rectangles/cuboids or concentric rings that prefer a free location near the center. |
| MET-026 | Planning | Research a world-conversion option for existing Metropolis worlds, including how to safely adapt existing regions and generated roads/plots. |
| GIT-009 | Planning | i have three reserve plots and one just got overwritten when a new player joined. |
| GIT-008 |   Ready  | Implement multiple plot sizes by granting multiple plots around each other. 
| GIT-007 | Planning | Allow buying and selling of homes via an ecnoomy plugin. |
| GIT-006 |   Done   | Add levels support. |
| GIT-005 | Planning | Add the possibility to create multiple plot homes in shapes. |
| GIT-004 | Done     | Add a command to request home allocation. |
| GIT-003 |   Ready  | Add support for multiple homes. |
| GIT-002 | Done     | Add a command to teleport to a reservation you are part of. |
| GIT-001 | Done     | Add a command to move to a different home. |

# GIT-001 - Add a command to move to a different home.
**Status:** Done
**Depends On:** [GIT-003](#git-003---add-support-for-multiple-homes)
**Description:**

`/metropolis-home-move <homeNumber> [playerName]` selects one of a player's existing homes as active. Without a player name, the command acts on the player who ran it; console and authorized administrators can specify an online or cached offline player. Reject non-positive or malformed numbers, unknown players, nonexistent homes, and extra arguments with clear feedback. Persist the selection by player UUID and confirm the change to the sender and, when online, the target player.

# GIT-002 - Add a command to teleport to a reservation you are part of.
**Status:** Done
**Depends On:** [GIT-003](#git-003---add-support-for-multiple-homes)
**Description:**

`/metropolis-plot-go [playerName]` teleports the sender, or the specified online player, to a Metropolis WorldGuard cuboid reservation where that player is an owner or member. If the player belongs to more than one reservation, report the available region names and require an explicit choice with `/metropolis-plot-go <reservationName> [playerName]`. The explicit-name form only permits destinations where the target is a member/owner, unless the sender has `metropolis.plot.go`. Remove the command-level permission gate so reservation members can use self-service teleportation; enforce admin-only target and membership bypass checks in the command. Report unknown players, missing membership, ambiguous reservations, and reservations without a safe teleport location. Do not report success unless teleportation succeeds.

### Comments

Or better yet just use uhome or create your own, integrated that records the home location at the height set in the configuration file, but on the road next to the plot. Using existing plugins to insert into SQL say uhome, would allow privatization and minimal work on your end.

# GIT-003 - Add support for multiple homes

**Status:** Done
**Description:**

Add a top-level `permissionOverrides` map keyed by permission node. Each rule defines `priority`, `plotMultiplier`, and `maxPlots`; any permission manager can grant the boolean nodes to groups or individual players. For online players, the matching rule with the highest priority wins, with the last matching entry in YAML order breaking ties. An explicit `userOverrides` entry takes precedence over permission rules. Without either override, use `plot.multiplier` and `plot.maxPerPlayer`. Offline-player generation uses username overrides or global defaults because permissions are only queried for online players.

On join, automatically allocate one home only when the player has no existing home, and restore a valid existing home as active if the saved selection is missing. Additional homes must be explicitly requested with `/metropolis-home-acquire`, up to the player's effective limit. Acquisition allocates the first unused positive home number and makes the new home active.

The alternative login allocation policies are tracked separately in MET-023 and MET-024; they are not part of the default policy in this task.

# GIT-004 - Add a command to request home allocation.
**Status:** Done
**Depends On:** [GIT-003](#git-003---add-support-for-multiple-homes)
**Description:**

`/metropolis-home-acquire` gives players their next available home, including the first home when `plot.initial` is `0`. The command allocates the lowest unused positive home number, makes it active, and refuses requests once the player has reached their effective limit. The default policy remains automatic first-home generation, but servers can disable it by setting `plot.initial: 0` and requiring the command for new players.

# GIT-005 - Add the possibility to create multiple plot homes in shapes.
**Status:** Planning
**Depends On:** [GIT-006](#git-006---add-levels-support), [MET-025](#met-025---add-a-configuration-option-to-choose-the-plot-allocation-traversal-algorithm-concentric-rectanglescuboids-or-concentric-rings-that-prefer-a-free-location-near-the-center)
**Description:**

Add the possibility to create multiple plot homes in contiguous shapes.

* Line all plots in a line x, y, or z.
* Rectangle with width (x), height(y), and length (z).
* Sphere closest to a point.

# GIT-006 - Add levels support.
**Status:** Done
**Description:**

Add multiple vertical levels to the city. Use the existing `road.level` as the base Y: the bottom of a ground-level plot is at that Y, and `plot.sizeY` defines the full plot height. For example, a five-block-high plot includes its floor at road level, three usable blocks, and a roof block; all five blocks belong to the plot region. Use the same plot-dimension-plus-road-spacing grid pitch vertically as horizontally; when road width is zero, adjacent plot volumes may touch. Do not allocate a plot if its full extent would exceed the world's buildable area. If the search cannot find any valid plot, log an allocation error and do not create the plot or region. The current config example is not intended for a multilevel world; request an updated `config.yml` before implementation. Existing `plot.offsetX`, `plot.offsetY`, and `plot.offsetZ` nudge the grid; `plot.multiplier`, `userOverrides[username].plotMultiplier`, and future `permissionOverrides[permission_name].plotMultiplier` affect plot size.

Add a maximum-level config value. If unset, default to 1; administrators can increase it to enable more levels. Allocate plots in X, Z, Y axis order: scan X from negative to positive, then increment Z, and only after scanning the X/Z area increment Y to the next level (Y outermost, Z middle, X innermost). For equal-distance candidates, use the same negative-to-positive X ordering. The selectable X/Z traversal shapes are tracked separately in MET-025.

Streets run along X and avenues run along Z. Streets remain level; avenues connect levels with stairs placed beside plots at a configurable cadence. Add `stairs.*`, `road.streets.*`, and `road.avenues.*` configuration. The street and avenue sections should each contain the current `road.*` settings plus `generateStairs`. Configure stair material, width, and `everyNBlocks`; this cadence is measured in plot-sized avenue blocks, so 3 means stairs beside every third plot along an avenue. Allow stairs to be configured independently for each direction, including different widths. Validate each enabled direction against its corresponding road width; if its stairs are not thinner than that road, log an error but continue plugin startup.

Keep spawn on its current level. Add independent X, Y, and Z spawn-size multipliers, each expressed as a multiple of the corresponding plot dimension and road spacing so roads line up with differently sized plots. The Y multiplier controls spawn's vertical height upward from `road.level`.

When allocating plots in an existing world, treat saved WorldGuard regions as occupied: never overwrite a region or create a new region that overlaps an existing one, even if avoiding overlaps leaves irregular gaps between plots and roads. Document this behavior so users know to expect it. Research a possible world-conversion option separately under MET-026; conversion requires further investigation.

This intentionally breaks the existing config schema: do not add a legacy `road.*` fallback or automatic migration. Document the config changes in release notes and make the breaking change in the 0.6.x release line.

# GIT-007 - Allow buying and selling of homes via an ecnoomy plugin.
**Status:** Blocked
**Blockers:** Figure out how to integrate with economy plugins.
**Description:**

Allow buying and selling of homes via an ecnoomy plugin. Include decent real estate support including wtb and wts listings as well as an auction facility.

This should also support not giving free plots to users with none. This way one would have to go out and mine to earn money which could be spent to purchase a plot.

# GIT-008 - Implement multiple plot sizes by granting multiple plots around each other.
**Status:** Ready
**Description:**

a= user a
x= empty plot
s= spawn

user a get's a small plot which is defined as 1 plot
a x x
x s x
x x x

user b gets a large plot which is defined as 4 plots
x x b b x
x a b b x
x x s x x
x x x x x
x x x x x

# MET-025 - Add a configuration option to choose the plot-allocation traversal algorithm: concentric rectangles/cuboids or concentric rings that prefer a free location near the center.
**Status:** Planning
**Depends On:** [MET-023], [MET-024]
