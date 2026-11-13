# Changelog for v0.5.2

<!--
  Release notes for the next release of Metropolis. Write Markdown below this comment.
  Everything under the header (HTML comments excluded) becomes the GitHub release and
  Modrinth changelog; the release version itself comes from the tag.
  After a release, reset this file for the next version and tag that commit
  v<next>-dev (see CONTRIBUTING.md).
-->

## Added

- **Multi-level plots.** Plots can be stacked vertically with `plot.maxLevels`; `plot.sizeY` is the height of one level. Upper-level plots are only allocated where every lower level beneath their footprint is already built, and the WorldGuard `City` region grows in all three dimensions to contain them. See `examples/multi-level.yml`.
- **Stairs between road levels.** Streets and avenues can generate stair runs up to the upper road (`stairs.generate`, `material`, `width`, `everyNBlocks`).
- **Plot grid offsets** (`plot.offsetX/Y/Z`) shift the logical plot grid; plot and road placement, levels, spawn alignment and indexing all follow it. Existing saved regions are not moved.
- **Configurable spawn size** with `spawn.sizeX`, `spawn.sizeY` and `spawn.sizeZ` (default `1`).
- **Multiple homes per player.** Players can own several homes up to a configurable limit and switch between them. Limits and plot sizes can come from `userOverrides` or from permission nodes via the new `permissionOverrides` section (highest `priority` wins; a username entry always wins).
- **Optional manual first home.** Set `plot.initial` to `0` to stop generating a home on join; players then run `/metropolis-home-acquire` when they want one.

## Changed

- **Breaking config change:** the `road` section is split into `road.streets` and `road.avenues`, each with its own width, level, material, clearance, supports and stairs. Move your old `road.*` values into both new sections before upgrading.
- `/metropolis-home-move <homeNumber> [player]` selects one of the player's existing homes. The choice is saved across restarts, and console or authorized admins can target online or cached offline players.
- `/metropolis-plot-go` now lets reservation owners and members teleport to their own reservations. Use `/metropolis-plot-go <reservationName> [player]` when you belong to several. The `metropolis.plot.go` permission is needed to target other players or to bypass membership.
- Plot allocation now treats every saved WorldGuard region as occupied, stays inside the world border, and expands the `City` region as plots are added.
- Roads are generated at full width when a plot is created, and neighboring plots no longer rewrite road blocks that are already correct. Gaps left by older half-width roads are filled.
- `/metropolis-debug-generatetesthomes <count>` (debug builds only) now validates its argument and generates the test homes in batches, refusing to start a second batch while one is running.
- The old `Examples/*.txt` files were replaced by `examples/multi-level.yml`.

## Development

- Large expansion of the unit test suite (road and stair layout, plot grid, levels, reservations, world border bounds, commands and listeners).
- Source is formatted with Spotless (google-java-format 1.36.1) and carries a license header.
- Added GitHub Actions for CI (build, test, upload jar) and draft GitHub releases from `v*` tags. Versions now come from `v*` Git tags and release notes come from this file.
- Added human test guidelines and reorganized the documentation.
