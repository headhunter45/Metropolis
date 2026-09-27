# Metropolis Human Testing

Use this checklist for behavior that depends on a real Paper world, player interactions, or the installed WorldGuard and WorldEdit plugins. Automated tests cover some isolated logic, but they do not establish that the complete in-game workflows behave correctly.

## Test Environment

- Use a disposable Paper 26.2 or 26.3 server with Java 25+, WorldGuard 7.0.19, WorldEdit 7.4.5, and Metropolis installed.
- Back up the server before testing. Home generation, reservations, and configuration can change world blocks and WorldGuard regions.
- Use at least two non-operator player accounts for ownership and protection checks. Operators can bypass protection and hide permission problems.
- Keep a server console open and check it for errors after startup, commands, joins, and restarts.
- Record the Metropolis config, world name, test player UUIDs, command output, and any unexpected region bounds or block changes.

## Human Checklist

### Startup and world setup

- Start with the required plugins enabled. Confirm Metropolis enables without errors and its commands are registered. Also confirm startup fails clearly if a required dependency or configured world is missing, then restore the valid setup.
- On a fresh test world, inspect the `City` and `Spawn` WorldGuard regions. Check their bounds, flags, and priority; verify the configured spawn generation and world-spawn location.
- Restart once with existing regions. Confirm Metropolis recognizes them instead of duplicating or resetting them, and that existing city bounds and player homes remain intact.

### Join and plot allocation

- Join as a new player. Confirm one home is allocated and the welcome message reports its actual bounds. Check the corresponding `h_1_<UUID>` region has the player as owner and its bounds match the configured plot size.
- Join with a second new player. Confirm the new home is distinct, does not overlap the first home or spawn, and the City region grows to include it.
- Join again as an existing player. Confirm the same home is selected and no duplicate region or plot is created.
- Reserve an area before allocating another home. Confirm future allocations skip it. Repeat with reservations near the city edge and near existing roads to look for overlaps or gaps.
- Test a user override that grants a different plot multiplier or home limit. Confirm the generated bounds and allowed number of homes match the override, and that the configured global defaults still apply to other players.

### Generated blocks and geometry

- Inspect a newly allocated home and adjoining roads. Check road width, level, material, clearance, plot floor, support blocks, and optional sign against the active config.
- Test generation over varied terrain, including water, caves, uneven ground, and existing structures. Confirm generated floors and roads do not leave unsafe gaps or erase blocks outside their intended area.
- Enable and disable optional floor, support, sign, spawn, world-spawn, and perimeter-wall settings one at a time on a disposable world. Confirm each setting changes only its intended behavior.
- Allocate plots in several directions and near the world origin. Check road joins, plot dimensions, City region expansion, and boundary coordinates for symmetry and off-by-one errors.

### Reservations and protection

- Reserve one plot with explicit coordinates. Then make a WorldEdit selection and reserve a second plot using `/metropolis-plot-reserve <name>`.
- Try invalid coordinates, a duplicate region name, and bounds that intersect a home or another reservation. Confirm the command reports a useful failure and does not silently overwrite an existing region.
- Check `/metropolis-home-list` and `/metropolis-plot-go <plotName>` for both occupied homes and reservations; confirm teleport destinations are safe and inside the requested plot.
- As the home owner, build and interact inside the home. As another non-op player, try the same actions. Verify ownership and protection behave as intended in homes, reservations, City, and Spawn.
- Verify representative protection flags in-game, including PvP, mob damage/spawning, explosions, and fire/lava behavior where applicable. Run `/metropolis-flag-reset` and confirm the expected flags are restored without changing region bounds or ownership.
- Restart the server and confirm reservations and their protection are still present in WorldGuard and remain excluded from home allocation.

### Commands, permissions, and player state

- For a new player with no overrides, join and confirm exactly one `h_1_<UUID>` home is created. Rejoin and confirm no additional home is created.
- Configure two `permissionOverrides` tiers in `plugins/Metropolis/config.yml`, then restart. Grant each permission node to a test user and to a test group through the server's permission manager. Confirm the player's effective `plotMultiplier` and `maxPlots` match the granted rule.
- Give one player multiple configured permission nodes with different priorities. Confirm the highest-priority rule controls both values; then give two matching rules the same priority and confirm the last matching YAML entry wins.
- Give a player both a permission tier and a matching `userOverrides` entry. Confirm the username override wins. Remove both overrides from another player and confirm `plot.multiplier` and `plot.maxPerPlayer` are used as defaults.
- Change or revoke a permission while the player is online, then acquire another home. Confirm the next acquisition uses the currently granted tier without requiring a restart.
- With plot multipliers of 1 and 2, acquire homes and inspect their WorldGuard bounds. Confirm size follows the tier, a larger footprint does not overlap an existing home, and the multiplier is applied to the X and Z footprint only once.
- Exercise `/metropolis-home-acquire` below and at each effective home limit. Confirm every successful request creates a distinct next-numbered home, creates none beyond the limit, and does not change other players' homes.
- After acquiring a second home, verify it becomes active: check the player's selected home in `plugins/Metropolis/currentHomes.yml` and use `/metropolis-home-go` to confirm travel to that home. Switch homes with `/metropolis-home-move` and verify the selection changes as expected.
- Restart after acquiring multiple homes. Confirm all home regions remain owned by the player, the active home selection persists, and joining does not allocate another first home.
- Generate a home for an offline player administratively. Confirm permission tiers are not assumed for offline players; the username override or global settings determine plot size and limit.
- Exercise `/metropolis-home-go` with a safe bed spawn inside the home and with no such bed spawn. Confirm teleportation chooses a valid location inside the home and reports a clear failure if no safe location exists.
- Exercise `/metropolis-home-move` for a player with multiple homes. Confirm the selected home persists across reconnect and restart; reject nonexistent home numbers.
- As an administrator, test `/metropolis-home-generate`, `/metropolis-home-evict`, and `/metropolis-home-list` with online and previously seen offline players. Confirm the intended region ownership changes, persistence, and subsequent allocation behavior.
- Test commands as console, permitted player, and player without the relevant permission. Check player-only commands, target-player arguments, unknown names, malformed arguments, and permission denials for useful feedback and no unintended changes.
- If upgrading an existing server, verify a legacy name-keyed `currentHomes.yml` entry resolves to the right UUID and the player's selected home is retained.

## Existing Automated Coverage

Run `./gradlew test` for the server-free JUnit suite. It has 17 tests covering:

- `CuboidTest`: WorldEdit bound conversion, vector ordering/null handling, touching intersections, and point containment.
- `CurrentHomesStoreTest`: UUID-based persistence and resolving a legacy player-name key.
- `PlayerJoinListenerTest`: the welcome bounds message when a home is supplied by a mock.
- `PlayerLookupTest`: case-insensitive full-name matching and rejection of prefix matching.
- `PlayerHomeTest`: UUID-based region identity and display-name retention.
- `HomePermissionOverrideTest`: dotted permission-node parsing, priority and tie resolution, invalid entries, and username/permission/default precedence.
- `HomeNumberAllocatorTest`: choosing the first unused positive home number.
- `MetropolisPlotReserveCommandTest`: parsing six coordinates and rejecting malformed coordinates.
- `MetropolisHomeAcquireTest`: acquisition below the limit and refusal at the limit.

The two opt-in tests in `src/integrationTest` require a running Paper server and RCON. They check that Metropolis appears in `/plugins` and that a coordinate reservation is persisted by WorldGuard. They do not currently verify restart persistence, join allocation, WorldEdit-selection reservations, permissions, teleports, generated blocks, protection behavior, or the other command workflows above. Run them with the environment variables documented in `README.md`; use a disposable server because the reservation test changes its WorldGuard data.

The integration scope described in `TODO.md` is broader than the tests currently present. Treat the scenarios in this guide as unverified until a human has run them on the target Paper and plugin versions.
