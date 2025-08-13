#!/bin/bash
# The purpose of this script is to automatically update the :shared/build.gradle.kts version
# field inside the publishing block with the provided parameter.

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi

VERSION_NAME="$1"
BUILD_FILE="shared/build.gradle.kts"

if [ ! -f "$BUILD_FILE" ]; then
  echo "Error: $BUILD_FILE not found!"
  exit 1
fi

echo "Updating version to: $VERSION_NAME"

if [[ "$OSTYPE" == "darwin"* ]]; then
  sed -i '' "s/version = \".*\"/version = \"$VERSION_NAME\"/" "$BUILD_FILE"
else
  sed -i "s/version = \".*\"/version = \"$VERSION_NAME\"/" "$BUILD_FILE"
fi

echo "✅ Version updated in $BUILD_FILE"