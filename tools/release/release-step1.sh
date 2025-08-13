#!/bin/bash
# The purpose of this script is to execute the 1st step in the release process.
#
# This consists of checking out the develop branch, updating it, and then creating the release/ branch
# that will be used for the rest of the release process.

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi
VERSION_NAME="$1"

# Ensure a clean working directory
if ! git diff-index --quiet HEAD --; then
  echo "Error: Working directory is not clean. Please commit or stash changes."
  exit 1
fi

# Ensure the branch doesn't exist already
if git show-ref --verify --quiet refs/heads/release/"$VERSION_NAME"; then
  echo "Error: Branch release/$VERSION_NAME already exists"
  exit 1
fi

echo "1️⃣ Commencing release process for version $VERSION_NAME..."

git checkout develop
git fetch
git pull
git status
git checkout -b release/"$VERSION_NAME"