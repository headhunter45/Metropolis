#!/usr/bin/env bash
set -e

# Find project root (one directory above this script)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

if [[ -x ./mvnw ]]; then
  ./mvnw package
else
  mvn package
fi
