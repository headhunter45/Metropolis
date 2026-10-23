# Metropolis Modernization Plan

This is the maintained status and follow-up plan. Implemented behavior is
described in [README.md](../README.md); automated and manual coverage are
separated in [human-tests.md](human-tests.md). A feature is not considered
runtime-verified merely because its code or an isolated unit test exists.

## Completed

- **Build and CI modernization.** Gradle 9.8 builds the Java 25 plugin and
  expands the version into `plugin.yml`. CI runs `test` and the plugin build
  for pushes and pull requests to `develop`. Completion check: running
  `./gradlew test build` succeeds; CI provides the corresponding hosted check.
- **Paper/WorldGuard/WorldEdit integration.** The plugin declares Paper API
  26.2, WorldGuard 7.0.19, and WorldEdit 7.4.5, and uses WorldGuard regions
  for city, home, and reservation geometry. Paper 26.2/26.3 are project
  targets; the current build and CI do not perform live server compatibility
  checks. Completion check for runtime claims: run the applicable manual
  scenarios in `human-tests.md` on a disposable server.
- **Plot, home, and command features.** The implementation includes join-time
  initial home allocation, explicit additional-home acquisition, UUID-based
  active-home persistence with legacy name-key loading, user/permission plot
  overrides, reservations, multi-level support checks, and configured
  road/stair generation. Isolated tests cover parts of these rules; see
  `src/test/` and `human-tests.md` for their boundaries.

## Pending

- **Expand live-server integration coverage.** The current `integrationTest`
  suite only checks plugin visibility and persistence of one coordinate-based
  WorldGuard reservation. Add focused disposable-server checks for join-time
  home allocation/ownership and selected-home restart persistence. Completion
  criteria: tests assert the expected region/UUID state before and after a
  restart, clean up their own test regions, and remain excluded from `build`.
- **Choose optional login allocation policies (MET-023/MET-024).** Current
  behavior allocates the configured initial home count (default one) when a
  player has no home; later homes require `/metropolis-home-acquire`. Decide
  whether to offer grant-all-to-limit or one-more-per-login policies before
  implementing them. Completion criteria: documented config semantics,
  compatibility defaults, and unit tests for limits, reconnects, and existing
  home selections.
- **Choose an allocation traversal option (MET-025).** The proposal to select
  a rectangle/cuboid or ring-based traversal remains unimplemented. First
  document deterministic ordering and how each policy treats occupied regions,
  levels, and WorldBorder bounds. Completion criteria: configuration and
  focused allocation tests establish repeatable ordering without overlaps.
- **Research conversion of existing worlds (MET-026).** There is no automatic
  world/config conversion workflow. Document the legacy data/config formats,
  backups and rollback requirements, and a safe migration strategy before
  proposing implementation. Completion criteria: a reviewed conversion
  specification plus disposable-world validation; do not claim migration
  support before that work is complete.

## Deferred Ideas

Economy-based home sales, arbitrary connected plot shapes, and alternate
multi-plot footprints remain product ideas, not committed work. Reconsider them
only after requirements, compatibility constraints, and acceptance criteria
are defined.