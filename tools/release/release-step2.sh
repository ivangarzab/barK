#!/bin/bash
# The purpose of this script is to execute the 2nd step in our release process.
#
# This consist of running the `update-lib-version.sh` script from the tools/ directory,
# and committing the changes that this script generates directly into our release branch.

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi
VERSION_NAME="$1"

echo "2️⃣ Updating library version to $VERSION_NAME..."

chmod +x tools/update-lib-version.sh

if ! ./tools/update-lib-version.sh "$VERSION_NAME"; then
  echo "Error: Failed to update library version"
  exit 1
fi

git add .
echo "Changes to commit:"
git --no-pager diff --cached
git commit -m "Update library's version for release v$VERSION_NAME"

echo "✅ Version updated and committed for release v$VERSION_NAME"