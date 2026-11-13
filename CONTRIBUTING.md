# Contributing to Metropolis

This is the single onboarding entry point for new contributors. Shared rules
for people and coding agents are in [AGENTS.md](AGENTS.md); read them before
editing, especially the scraper-metadata preservation and operational-safety
rules.

## First Contribution

1. Read [README.md](README.md) for supported server versions, installation,
	 configuration, and commands.
2. Trace the relevant implementation using the source map below and check
	 nearby tests before changing behavior.
3. Make a focused change. Add or update automated tests for behavior changes;
	 update the manual pre-release checklist for behavior that needs a real
	 server.
4. Run `./gradlew test` and `./gradlew build`. Report any manual scenarios that
	 were not run rather than describing them as passing.
5. Update user-facing descriptions if setup, supported versions, commands,
	 dependencies, or behavior changed.

## Architecture and Source Map

- `MetropolisPlugin` loads configuration, connects Bukkit to WorldGuard and
	WorldEdit, allocates plots, generates blocks, and manages regions.
- `Plot`, `PlayerHome`, grid/layout/support classes, and home stores implement
	plot geometry, region ownership, allocation, and selected-home persistence.
- `commands/` contains the command handlers; `eventlisteners/` contains join
	behavior.
- `src/main/resources/plugin.yml` declares dependencies, commands, and command
	permissions. `config.yml` is the bundled configuration template.
- `src/test/` contains isolated JUnit/Mockito tests. `src/integrationTest/`
	contains the opt-in tests that require a running server and RCON.
- `build.gradle` defines dependencies, Java/Gradle versions, formatting, and
	test tasks. `.github/workflows/gradle.yml` runs tests and builds on changes
	to `main` and `develop`, and any `v*` tags.

## Prerequisites and Checks

- JDK 25. The Gradle wrapper uses Gradle 9.8 and can download the required
	Gradle distribution; dependencies resolve from Maven Central, PaperMC, and
	EngineHub.
- `./gradlew test` runs server-free JUnit tests.
- `./gradlew build` runs the normal build and verification lifecycle. The
	plugin jar is written under `build/libs/`.
- CI compiles against Paper API 26.2, runs `test`, then builds. It does not
	start a Minecraft server or run `integrationTest`.

The opt-in `integrationTest` source set currently has two tests: checking that
Metropolis appears in `/plugins`, and creating a coordinate reservation then
checking WorldGuard's `regions.yml`. It mutates the configured server's
WorldGuard data and needs a running Paper server with RCON, WorldGuard, and
WorldEdit. Use a disposable server, bind RCON to a trusted interface, and use
a temporary password:

```sh
METROPOLIS_RCON_HOST=127.0.0.1 \
METROPOLIS_RCON_PORT=25575 \
METROPOLIS_RCON_PASSWORD=... \
METROPOLIS_SERVER_DIR=/path/to/server \
./gradlew integrationTest
```

Set `METROPOLIS_WORLD` if the test world is not named `world`. These tests do
not cover the broader in-game workflows in [human-tests.md](docs/human-tests.md).

## Operational Safety

Use the build/test commands above for ordinary contribution work. The scripts
under `tools/` also include server start/stop, deployment, backup/restore, and
publishing operations; they are not required for a normal build. Read a
script before use and get explicit authorization before running operational
or release actions. In particular, `stop-server.sh` matches Paper jar
processes by command line, and `restore-server.sh` replaces matching top-level
server items from an archive. Never commit `.env`, credentials, server state,
or generated build output.

## Optional References

- [README.md](README.md): player/server-admin usage reference.
- [human-tests.md](docs/human-tests.md): manual pre-release checklist, not an
	automated test report.
- [TODO.md](docs/TODO.md): historical backlog; the maintained plan is
	[modernization-plan.md](docs/modernization-plan.md).
- [Modrinth description](docs/modrinth-description.md): hosting copy reference.
- [Scraper metadata](docs/project-description.md) and
	[schema](docs/projects.schema.json): consuming-site project entry.

## Versioning, changelog, and releases

**Version strings** come from Git tags named `v<major>.<minor>.<patch>[-suffix]` and
are computed by [tools/compute-version.sh](tools/compute-version.sh). Gradle (and so
`plugin.yml`), CI, and the release workflow all call it, so they always agree.
`tools/compute-version.sh --explain` shows how a version was chosen.

- A tagged commit uses its tag. With several tags the greatest release tag (no
  suffix) wins; if all have suffixes, the greatest of those wins.
- An untagged commit looks at every tag reachable from it and takes the greatest.
  If that tag ends in `-dev` the version is that tag; otherwise it is the tag with
  the patch number bumped by one and `-dev` added (`v0.2.2` builds as `0.2.3-dev`).
- With no tags at all the version is `0.0.1-dev`.

**[changelog.md](changelog.md)** holds the release notes for the next release. Its
first-level header names the version (`# Changelog for v0.2.3`). The tag
decides the released version (a release build warns if the header names a different
one), and everything under the header (HTML comments excluded) becomes the GitHub
and Modrinth release notes; a release with empty notes is refused.
[tools/changelog.sh](tools/changelog.sh) reads the header and extracts the notes.

**CI** ([gradle.yml](.github/workflows/gradle.yml)) runs the `Build and Test` job
for pushes and pull requests to `main` and `develop` and for `v*` tags. Make
`Build and Test` a required status check on `main` and `develop`.
Pushing a `v*` tag that is not `-dev` also creates or updates a **draft GitHub
release** with the built jar and the notes from `changelog.md`. Tags ending in `-dev`
never get a release.

**Releasing** ([release.yml](.github/workflows/release.yml), run manually with the
tag): it refuses `-dev` tags, tags whose commit would build as a different version,
and commits without a successful `Build and Test` run, saying why. Otherwise it
fast-forwards `main`, creates `release/<tag>`, rebuilds, publishes to Modrinth, and
publishes the GitHub release (promoting the draft). Tags must be on a commit that
already contains the files above.

**After a release**, commit a reset `changelog.md` with the next expected version
(release version + 0.0.1) and tag that commit `v<next>-dev`.
