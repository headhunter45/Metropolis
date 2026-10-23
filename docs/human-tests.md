# Metropolis Manual Pre-release Checklist

This is a manual checklist of expected behavior that needs a real Paper world,
player interaction, or installed WorldGuard/WorldEdit plugins. Checklist items
are scenarios to run, not test results; no item is considered passed until a
person performs it and records the outcome. Automated coverage is summarized
separately below.

## Test Environment

- Use a disposable Paper 26.2 or 26.3 server with Java 25+, WorldGuard 7.0.19, WorldEdit 7.4.5, and Metropolis installed.
- Back up the server before testing. Home generation, reservations, and configuration can change world blocks and WorldGuard regions.
- Use at least two non-operator player accounts for ownership and protection checks. Operators can bypass protection and hide permission problems.
- Keep a server console open and check it for errors after startup, commands, joins, and restarts.
- Record the Metropolis config, world name, test player UUIDs, command output, and any unexpected region bounds or block changes.

## Manual Scenarios

### Startup and world setup

- Start with the required plugins enabled. Confirm Metropolis enables without errors and its commands are registered. Also confirm startup fails clearly if a required dependency or configured world is missing, then restore the valid setup.
- On a fresh test world, inspect the `City` and `Spawn` WorldGuard regions. Check their bounds, flags, and priority; verify the configured spawn generation and world-spawn location.
- Restart once with existing regions. Confirm Metropolis recognizes them instead of duplicating or resetting them, and that existing city bounds and player homes remain intact.

### Join and plot allocation

- Join as a new player. Confirm one home is allocated and the welcome message reports its actual bounds. Check the corresponding `h_1_<UUID>` region has the player as owner and its bounds match the configured plot size.
- Set `plot.initial: 0` on a disposable server, restart, and join as a fresh player. Confirm no home is created until the player runs `/metropolis-home-acquire`; then verify the command creates the initial home and makes it active.
- Join with a second new player. Confirm the new home is distinct, does not overlap the first home or spawn, and the City region grows to include it.
- Start from a disposable world configured with `plot.sizeY` and `plot.maxLevels` from the multi-level example. Confirm the level count is honored, the city does not exceed the world's build height, and upper plots are only allocated above footprints fully supported at every lower level. With a 1×1 spawn and a 2×2 home size, confirm a new home is allocated at base level rather than above the partially supported spawn.
- Generate homes at base and elevated levels, then verify each full home cuboid is inside the WorldGuard `City` region. Add a home/reservation extending the city bounds and confirm City expands without shrinking its existing bounds or flags.
- Set positive and negative `plot.offsetX`, `plot.offsetY`, and `plot.offsetZ` values on a disposable world. Verify plot regions, road bands, stair flights, and Spawn share the shifted grid origin; allocate multiple plots across negative coordinates and confirm logical-cell spacing stays consistent. Confirm changing offsets does not move existing saved regions; Metropolis-managed homes and reservations should retain their bounds and remain recognized. Treat automatic conversion of other saved regions as unverified.
- Set `spawn.sizeX`, `spawn.sizeY`, and `spawn.sizeZ` to distinct positive values on a fresh disposable world. Confirm the Spawn region spans the corresponding plot-plus-road logical cells horizontally and the configured plot-height multiple vertically. Check blocks outside those bounds remain unchanged; configure a height exceeding the world's build limit and confirm startup rejects it clearly.
- Join again as an existing player. Confirm the same home is selected and no duplicate region or plot is created.
- Reserve an area before allocating another home. Confirm future allocations skip it. Repeat with reservations near the city edge and near existing roads to look for overlaps or gaps.
- Create an unrelated saved WorldGuard region in the next candidate area, then allocate a home and confirm it is skipped. Repeat with a non-cuboid region and verify the full bounding cuboid is avoided; confirm `City` itself does not prevent allocation.
- Set a small WorldBorder in a disposable world, occupy or reserve the remaining valid grid cells, and request another home. Confirm search grows beyond 256 rings but never places a plot/road footprint outside the border; when no supported cell remains, check the error log and confirm no region is created.
- Test a user override that grants a different plot multiplier or home limit. Confirm the generated bounds and allowed number of homes match the override, and that the configured global defaults still apply to other players.

### Generated blocks and geometry

- Inspect a newly allocated home and adjoining roads. Check road width, level, material, clearance, plot floor, support blocks, and optional sign against the active config.
- Generate one plot in a disposable world and confirm each adjoining road is fully paved to the configured width. Generate a plot directly beside it: confirm already-correct shared road blocks are untouched, any missing half-width legacy section is filled, and stair treads/openings remain intact.
- Configure streets and avenues with different widths, materials, clearances, and support settings. Verify streets along X use only the street options and avenues along Z use only avenue options.
- Enable stairs for both sections with different widths/cadences, each narrower than its road. Generate a plot on a level below `plot.maxLevels`; verify street stairs ascend along positive X at north/south borders and avenue stairs ascend along positive Z at east/west borders. Confirm lower roads remain unchanged, the first treads start one block above and forward from the road-level landing, connected treads use inverted backing, and top treads reach the upper road Y. Check each upper road leaves exactly three blocks open before its top tread only across that stair width. For a 2-plot-wide/long home with cadence 1, confirm two centered runs along each eligible edge. Repeat with higher levels to confirm existing flights remain open.
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
- Add a non-op test player as a WorldGuard member of a Metropolis reservation. Run `/metropolis-plot-go` and confirm they arrive inside that reservation at a safe location; repeat with an owner.
- Confirm a player who is neither owner nor member cannot use a reservation name to teleport there. Check that a region they own/member outside homes, City, and Spawn is treated as expected by the Metropolis reservation lookup.
- Add a player to two reservations. Confirm the no-argument form reports both names without teleporting, then specify one reservation and verify the chosen destination.
- As an authorized admin, target another online member with `/metropolis-plot-go <playerName>`. Confirm the target receives a notification. Repeat without `metropolis.plot.go` and verify targeting another player is denied.
- Test a reservation with no safe destination. Confirm the command reports failure and does not claim teleportation succeeded. Also test console use with an online player name and an unknown/offline name.

### Commands, permissions, and player state

- For a new player with no overrides, join and confirm exactly one `h_1_<UUID>` home is created. Rejoin and confirm no additional home is created.
- Configure two `permissionOverrides` tiers in `plugins/Metropolis/config.yml`, then restart. Grant each permission node to a test user and to a test group through the server's permission manager. Confirm the player's effective `plotMultiplier` and `maxPlots` match the granted rule.
- Give one player multiple configured permission nodes with different priorities. Confirm the highest-priority rule controls both values; then give two matching rules the same priority and confirm the last matching YAML entry wins.
- Give a player both a permission tier and a matching `userOverrides` entry. Confirm the username override wins. Remove both overrides from another player and confirm `plot.multiplier` and `plot.maxPerPlayer` are used as defaults.
- Change or revoke a permission while the player is online, then acquire another home. Confirm the next acquisition uses the currently granted tier without requiring a restart.
- With plot multipliers of 1 and 2, acquire homes and inspect their WorldGuard bounds. Confirm size follows the tier, a larger footprint does not overlap an existing home, and the multiplier is applied to the X and Z footprint only once.
- Exercise `/metropolis-home-acquire` below and at each effective home limit. Confirm every successful request creates a distinct next-numbered home, creates none beyond the limit, and does not change other players' homes.
- After acquiring a second home, verify it becomes active: check the player's selected home in `plugins/Metropolis/currentHomes.yml` and use `/metropolis-home-go` to confirm travel to that home. Switch homes with `/metropolis-home-move` and verify the selection changes as expected.
- Run `/metropolis-home-move <homeNumber>` as a player with multiple homes. Verify the command confirms the selected home, updates `currentHomes.yml`, and `/metropolis-home-go` now travels to it.
- Run `/metropolis-home-move <homeNumber> <playerName>` as an authorized administrator, then repeat from console. Check an online target is notified and a cached offline target uses the selected home on their next join.
- Try zero, negative, non-numeric, nonexistent home numbers, unknown player names, and extra arguments. Confirm each request is rejected with useful feedback and does not change the stored active home.
- Restart after acquiring multiple homes. Confirm all home regions remain owned by the player, the active home selection persists, and joining does not allocate another first home.
- Generate a home for an offline player administratively. Confirm permission tiers are not assumed for offline players; the username override or global settings determine plot size and limit.
- Exercise `/metropolis-home-go` with a safe bed spawn inside the home and with no such bed spawn. Confirm teleportation chooses a valid location inside the home and reports a clear failure if no safe location exists.
- Exercise `/metropolis-home-move` for a player with multiple homes. Confirm the selected home persists across reconnect and restart; reject nonexistent home numbers.
- As an administrator, test `/metropolis-home-generate`, `/metropolis-home-evict`, and `/metropolis-home-list` with online and previously seen offline players. Confirm the intended region ownership changes, persistence, and subsequent allocation behavior.
- Test commands as console, permitted player, and player without the relevant permission. Check player-only commands, target-player arguments, unknown names, malformed arguments, and permission denials for useful feedback and no unintended changes.
- If upgrading an existing server, verify a legacy name-keyed `currentHomes.yml` entry resolves to the right UUID and the player's selected home is retained.

## Automated Coverage (Separate from This Checklist)

Run `./gradlew test` for the server-free JUnit suite. It covers isolated plot
geometry/allocation rules, road and stair layouts/build plans, WorldGuard
region occupancy and reservation lookup, spawn/city bounds, home identity and
selection persistence, permission overrides, player lookup, and selected
command/event behavior. These unit tests do not load a real Paper server.

The separate `src/integrationTest` source set currently contains two live
checks: Metropolis appears in `/plugins`, and a coordinate reservation is
persisted in WorldGuard's `regions.yml`. It does not verify restart behavior,
join allocation, WorldEdit-selection reservations, permissions, teleport
workflows, generated blocks, or protection behavior. See
[CONTRIBUTING.md](../CONTRIBUTING.md) for prerequisites and safe invocation.
Those integration checks have not been run as part of this documentation
update. The manual scenarios above remain unverified until individually run
and recorded against the target server/plugins.
