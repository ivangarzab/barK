#!/bin/bash
# The purpose of this script is to execute the 4th step in our release process.
#
# This consists of finishing the branch update process for both 'main' & 'develop',
# followed by simply pushing all changes up to the 'origin,' including the newly created tag.

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 1.0.0"
  exit 1
fi
VERSION_NAME="$1"

# Verify tag exists
if ! git tag -l | grep -q "^$VERSION_NAME$"; then
  echo "Error: Tag $VERSION_NAME does not exist"
  exit 1
fi

echo "4️⃣ Pushing everything to origin..."

# Finish updating 'develop' and push to 'origin'
git checkout develop
git merge --no-ff -m "Merge main into develop after release v$VERSION_NAME" main
git push origin develop

# Checkout 'main' and push to 'origin'
git checkout main
git push origin main

# Push new tag as well
git push origin "$VERSION_NAME"

echo "✅ All changes and tags pushed to origin successfully"