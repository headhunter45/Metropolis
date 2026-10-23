# Metropolis Working Rules

Start with [CONTRIBUTING.md](CONTRIBUTING.md) for the first-contribution path,
architecture, build/test commands, and operational safety. [README.md](README.md)
is the player/server-admin behavior reference. Other files in `docs/` are
optional reference material, not competing onboarding guides.

- Use **Metropolis** in human-facing prose. Preserve machine-facing identifiers,
  including `Metropolis`, `com.majinnaibu.bukkitplugins.metropolis`, artifact
  names, plugin IDs, commands, permission nodes, and URLs.
- Inspect the current diff before editing and preserve unrelated work. Do not
  edit generated `build/`, `target/`, or `.gradle/` output. Never read, expose,
  or commit `.env`, server/player data, or publishing credentials.
- Keep claims aligned with source, build configuration, CI, and scripts. State
  Paper targets separately from versions actually built or tested. Keep
  implemented behavior, planned work, automated coverage, and manual scenarios
  distinct.
- When editing Java, preserve the existing AGPL header, use Gradle Spotless,
  and keep Bukkit/Paper state and API access on the main thread. Avoid unrelated
  gameplay changes.
- Before editing `docs/project-description.json`, read
  `docs/projects.schema.json`. Validate the JSON against that schema without
  changing the schema to accommodate invalid metadata. Preserve exactly the
  existing top-level project key, `id`, `title`, `docId`, `image.url`, and
  `headerImage.url` when present. Preserve `headerImage: null` or its absence.
  These are fixed consuming-project integration values: do not derive or
  “correct” their spelling, capitalization, spacing, or slug from repository
  names. Image paths are relative to the consuming project's root; do not
  resolve them here, convert them to repository URLs, or replace them with
  generated/remote URLs. Preserve all other integration fields unless the task
  requires a change. If schema compliance conflicts with preserving protected
  values, stop and explain the conflict before editing.
- Do not start/stop servers, deploy, restore, publish, tag, or release without
  explicit authorization. `tools/stop-server.sh` can match unrelated Paper
  processes; `tools/restore-server.sh` replaces archive top-level items.
