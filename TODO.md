# Modernize Metropolis Plugin: Implementation Tasks

|   ID    |  Status  | Title |
|:-------:|:--------:|:------|
| MET-000 |   Ready  | Add Gradle plugins as needed (e.g., Shadow) |
| MET-000 |   Ready  | Add repositories and dependencies |
| MET-000 |   Ready  | Address any bugs or incompatibilities found during testing on a modern server.
| MET-000 |   Ready  | Analyze all Java source files for deprecated or removed Bukkit API usage, including logger, event registration, listener classes, configuration API, and entity/player mapping. |
| MET-000 |   Ready  | Build the plugin with Gradle (./gradlew build) and test on a modern Paper server using the provided bash scripts.
| MET-000 |   Ready  | Configure Java version |
| MET-000 |   Ready  | Ensure all commands are properly defined in plugin.yml |
| MET-000 |   Ready  | Ensure plugin.yml is present in src/main/resources and update for PaperMC compatibility (api-version, commands, required fields). |
| MET-000 |   Ready  | Ensure resource handling for plugin.yml |
| MET-000 |   Ready  | Identify and document any hardcoded Bukkit references or legacy patterns (e.g., Player as HashMap key, old event registration, CraftBukkit class names). |
| MET-000 |   Ready  | Initialize Gradle in the project root and create a build.gradle file with project metadata, PaperMC API dependency, JUnit, Java toolchain, repository, resource handling, and plugins as needed. |
| MET-000 |   Ready  | Refactor all event listeners to use the modern event system (Listener interface, @EventHandler, registerEvents).
| MET-000 |   Ready  | Refactor all logger usage to use getLogger() from JavaPlugin. |
| MET-000 |   Ready  | Remove Maven-specific files (pom.xml, .mvn/ directory, Maven wrapper scripts) and Eclipse-specific files (.classpath, .project, .settings/) if present. |
| MET-000 |   Ready  | Remove or modernize any old/deprecated event registration (use @EventHandler and registerEvents) |
| MET-000 |   Ready  | Remove or refactor any code using deprecated or removed Bukkit/Spigot/Paper APIs that are not available in the modern Paper API.
| MET-000 |   Ready  | Replace all usage of org.bukkit.util.config.Configuration with the modern Bukkit configuration API (getConfig(), saveConfig(), reloadConfig(), etc.).
| MET-000 |   Ready  | Review and list any custom scripts or resources (e.g., bash scripts in tools/bash/) that may need migration or updates.|
| MET-000 |   Ready  | Review and update player lookup logic to use getPlayerExact or handle case sensitivity |
| MET-000 |   Ready  | Set project metadata in build.gradle |
| MET-000 |   Ready  | Switch all player score storage to use UUID instead of Player as the key |
| MET-000 |   Ready  | Test the Gradle build |
| MET-000 |   Ready  | Update .gitignore to add Gradle-specific ignores and remove Maven/Eclipse-specific ignores. |
| MET-000 |   Ready  | Update README.md and CONTRIBUTING.md with new build, usage, and development instructions. _(Depends on: bugfixes)_
| MET-000 |   Ready  | Update documentation and scripts |
| MET-000 |   Ready  | Update logger usage to use getLogger() from JavaPlugin |
| MET-000 |   Ready  | (Optional) Add new features, quality-of-life improvements, automated tests, or CI configuration. _(Depends on: docs update)_
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
