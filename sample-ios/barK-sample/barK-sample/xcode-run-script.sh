# bash
cd "$SRCROOT/../.."
echo "🐕 Starting barK framework build..."
export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
echo "✅ Framework built"