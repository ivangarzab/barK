#!/bin/bash
#
# The purpose of this file is to be used inside XCode, in the app target's Build Phases section as a Run Script.
#
# This script should be ran before compilation step of the app's build process,
# as to ensure we continuously build the Kotlin shared code alongside the iOS app.

# Navigate to the project root (two levels up from sample-ios/barK-sample)
cd "$SRCROOT/../.."

echo "🐕 Starting barK framework build..."

if [ -d "/Applications/Android Studio.app/Contents/jbr/Contents/Home" ]; then
    export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
fi

./gradlew :shared:embedAndSignAppleFrameworkForXcode

echo "✅ Framework build completed successfully"
