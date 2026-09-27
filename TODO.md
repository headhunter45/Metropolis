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
| GIT-009 | Planning | i have three reserve plots and one just got overwritten when a new player joined. |
| GIT-008 |   Ready  | Implement multiple plot sizes by granting multiple plots around each other. 
| GIT-007 |   Ready  | Allow buying and selling of homes via an ecnoomy plugin. |
| GIT-006 |   Ready  | Add levels support. |
| GIT-005 | Planning | Add the possibility to create multiple plot homes in shapes. |
| GIT-004 |   Ready  | Add a command to request home allocation. |
| GIT-003 |   Ready  | Add support for multiple homes. |
| GIT-002 |   Ready  | Add a command to teleport to a reservation you are part of. |
| GIT-001 |   Ready  | Add a command to move to a different home. |

# GIT-001 - Add a command to move to a different home.
**Status:** Ready
**Descriptioon:** 

/metropolis-home-move [playerName]
The target player will be the callee or if specified [playerName].
This command should set the target player's home to the plot defined by .

# GIT-002 - Add a command to teleport to a reservation you are part of.
**Status:** Ready
**Description:**

/metropolis-plot-go [playerName]
The target player should be [playerName] if specified and otherwise should be the callee.
The reservation to teleport to is

### Comments

Or better yet just use uhome or create your own, integrated that records the home location at the height set in the configuration file, but on the road next to the plot. Using existing plugins to insert into SQL say uhome, would allow privatization and minimal work on your end.

# GIT-003 - Add support for multiple homes
 
**Status:** Ready
**Description:**

The max number of homes per player should be defined by a permission such as
metropolis.maxhomes: 2

A config option should be added to determine when the homes are added with at least the following options

Allocate all homes that aren't allocated on login
Allocate at most 1 home per login
Only allocate the first home on login, and require a command to allocate additional homes.

# GIT-004 - Add a command to request home allocation.
**Status:** Ready
**Description:**

Add a command that users can type to get their first or subsequent homes.
Add a config option to not generate any homes automatically, but to require this command to generate a user's first home.

This could be used so that homes won't be generated until a user has read the server rules which would include telling them to type the command towards the end.

# GIT-005 - Add the possibility to create multiple plot homes in shapes.
**Status:** Planning
**Description:**

Add the possibility to create multiple plot homes in shapes


# GIT-006 - Add levels support.
**Status:** Ready
**Description:**

Allow generating multilevel cities with stairs or interchanges to go between levels (streets are level but avenues are ramps.

# GIT-007 - Allow buying and selling of homes via an ecnoomy plugin.
**Status:** Ready
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
