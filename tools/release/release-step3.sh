#!/bin/bash
# The purpose of this script is to execute the 3rd step in our release process.
#
# This consists of pushing the release branch up into the 'origin', and then merging that
# same branch into both 'main' & 'develop'.

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi
VERSION_NAME="$1"

BRANCH_NAME=release/"$VERSION_NAME"
DATE_TODAY=$(date +"%Y-%m-%d")

# Ensure the release/$VERSION_NAME branch exists
if ! git show-ref --verify --quiet refs/heads/"$BRANCH_NAME"; then
  echo "Error: Branch $BRANCH_NAME does not exist"
  exit 1
fi

# Ensure a clean working directory
if ! git diff-index --quiet HEAD --; then
  echo "Error: Working directory is not clean. Please commit or stash changes."
  exit 1
fi

echo "3️⃣ Merging and tagging  release $VERSION_NAME..."

# Publish release branch into 'origin'
git push -u origin "$BRANCH_NAME"

# Merge release branch into 'main'
git checkout main
git merge --no-ff -m "Merge $BRANCH_NAME into main" "$BRANCH_NAME"

# Tag the release
git tag -a "$VERSION_NAME" -m "Released v$VERSION_NAME on $DATE_TODAY"

# Merge 'main' branch into 'develop'
git checkout develop
git merge --no-ff -m "Merge main into develop after release $VERSION_NAME" main

echo "✅ Release process completed and ready to be pushed"

