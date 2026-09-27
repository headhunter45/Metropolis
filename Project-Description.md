# Metropolis

Metropolis is a Paper plugin that expands a protected Minecraft city as players join. It generates home plots and connecting roads, uses WorldGuard to protect the city and homes, and supports reserved plots and home teleportation.

## Platform

- Paper 26.2, Java 25+
- WorldGuard 7.0.19 and WorldEdit 7.4.5
- Built with Gradle 9.8 and Git-sensitive semantic versioning
- Configuration: `plugins/Metropolis/config.yml`
- Unit tests run without a server; live integration tests use the separate Gradle `integrationTest` task.
- License: GNU General Public License v3