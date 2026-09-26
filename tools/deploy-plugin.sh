#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# shellcheck source=setup-env.sh
source "$SCRIPT_DIR/setup-env.sh"

if [[ -z "$MINECRAFT_SERVER_PATH" ]]; then
  echo "Error: MINECRAFT_SERVER_PATH environment variable is not set."
  exit 1
fi

# Skip Maven side artifacts (shade originals, sources, javadoc).
PLUGIN_JAR=$(ls -t "$PROJECT_ROOT"/target/*.jar 2>/dev/null \
  | grep -vE '/original-[^/]*\.jar$|-(sources|javadoc|tests)\.jar$' | head -n1 || true)
if [[ ! -f "$PLUGIN_JAR" ]]; then
  echo "Error: No plugin jar found in $PROJECT_ROOT/target. Build the plugin first."
  exit 1
fi

if find "$PROJECT_ROOT/src/main/java" "$PROJECT_ROOT/src/main/resources" -type f -newer "$PLUGIN_JAR" | grep -q .; then
  echo "Warning: The built plugin jar is older than some source files. Consider rebuilding."
fi

mkdir -p "$MINECRAFT_SERVER_PATH/plugins"
cp "$PLUGIN_JAR" "$MINECRAFT_SERVER_PATH/plugins/"
echo "Deployed $PLUGIN_JAR to $MINECRAFT_SERVER_PATH/plugins/"
