#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# shellcheck source=setup-env.sh
source "$SCRIPT_DIR/setup-env.sh"

MODRINTH_TOKEN="${MODRINTH_TOKEN:-}"
MODRINTH_PROJECT_ID="${MODRINTH_PROJECT_ID:-${MODRINTH_PROJECT_SLUG:-metropolis-builder}}"
MODRINTH_VERSION="${MODRINTH_VERSION:-${VERSION:-$(grep -E '<version>|<project>' pom.xml | head -n2 | tail -n1 | sed -E 's#.*<version>([^<]+)</version>.*#\1#')}}"
MODRINTH_CHANGELOG="${MODRINTH_CHANGELOG:-Auto release via publish-modrinth.sh}"
MODRINTH_GAME_VERSIONS="${MODRINTH_GAME_VERSIONS:-1.21,1.21.1,1.21.2,1.21.3,1.21.4,1.21.5,1.21.6,1.21.7}"
MODRINTH_LOADERS="${MODRINTH_LOADERS:-paper}"
MODRINTH_FILE="${MODRINTH_FILE:-}"
MODRINTH_DEPENDENCIES="${MODRINTH_DEPENDENCIES:-}"

if [[ -z "$MODRINTH_TOKEN" ]]; then
  echo "Error: MODRINTH_TOKEN is not set in $PROJECT_ROOT/.env" >&2
  exit 1
fi

if [[ -z "$MODRINTH_FILE" ]]; then
  MODRINTH_FILE=$(find "$PROJECT_ROOT/target" -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n1 || true)
fi
if [[ -z "$MODRINTH_FILE" || ! -f "$MODRINTH_FILE" ]]; then
  echo "Error: no jar found for Modrinth upload. Build the project first or set MODRINTH_FILE." >&2
  exit 1
fi

csv_to_json_array() {
  jq -cn --arg csv "$1" '$csv | split(",") | map(gsub("^\\s+|\\s+$"; "")) | map(select(length > 0))'
}
MODRINTH_GAME_VERSIONS_JSON=$(csv_to_json_array "$MODRINTH_GAME_VERSIONS")
MODRINTH_LOADERS_JSON=$(csv_to_json_array "$MODRINTH_LOADERS")

if [[ -n "$MODRINTH_DEPENDENCIES" ]]; then
  MODRINTH_DEPENDENCIES_ARG=(--form "dependencies=$MODRINTH_DEPENDENCIES")
else
  MODRINTH_DEPENDENCIES_ARG=()
fi

curl -fsSL \
  -X POST "https://api.modrinth.com/v2/version" \
  -H "Authorization: $MODRINTH_TOKEN" \
  -F "project_id=$MODRINTH_PROJECT_ID" \
  -F "version_number=$MODRINTH_VERSION" \
  -F "changelog=$MODRINTH_CHANGELOG" \
  -F "game_versions=${MODRINTH_GAME_VERSIONS_JSON}" \
  -F "loaders=${MODRINTH_LOADERS_JSON}" \
  -F "file=@$MODRINTH_FILE" \
  "${MODRINTH_DEPENDENCIES_ARG[@]}" \
  | jq .
