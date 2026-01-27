# CLAUDE.md - barK Project Guide

**Last updated:** 2026-01-27
**Current Version:** 0.1.3
**Purpose:** Quick reference for Claude Code when working with the barK logging library

---

## Project Overview

**barK** is a lightweight, extensible logging library for Kotlin Multiplatform with automatic tag detection and smart test environment handling. Published on Maven Central as `com.ivangarzab:bark`.

**Key Features:**
- Cross-platform support (Android + iOS with platform parity)
- Automatic tag detection from stack traces
- Smart test environment detection
- Multiple concurrent log handlers ("Trainers")
- Runtime control (muzzle/unmuzzle)
- Colored console output for tests
- Dog-themed API

**Philosophy:** SDK-friendly, minimal dependencies, extensible by design.

---

## Project Structure

```
barK/
├── shared/                    # Main KMP library (THIS IS THE LIBRARY)
│   ├── src/
│   │   ├── commonMain/       # Platform-agnostic API
│   │   ├── androidMain/      # Android implementations
│   │   ├── iosMain/          # iOS implementations
│   │   ├── commonTest/       # Shared tests
│   │   ├── androidUnitTest/  # Android tests (18 files)
│   │   └── iosTest/          # iOS tests (6 files)
│   └── build.gradle.kts      # Library build & publishing
├── ios/                       # iOS distribution (BarkExtensions.swift)
├── sample-android/            # Android demo app
├── sample-ios/                # iOS demo app (12 Swift files + 3 test files)
├── tools/                     # Release automation scripts
└── .github/workflows/         # CI/CD pipelines
```

**Important:**
- Library code is in `shared/`, not root
- Sample apps are demos only (not published)
- `/ios` contains BarkExtensions.swift for cleaner Swift API (user-copied file)

---

## Core Architecture

### The Bark Object

Singleton API entry point at `shared/src/commonMain/.../Bark.kt`:
- **Logging:** `v()`, `d()`, `i()`, `w()`, `e()`
- **Trainers:** `train()`, `untrain()`, `releaseAllTrainers()`
- **Control:** `muzzle()`, `unmuzzle()`
- **Tags:** `tag()`, `untag()`
- **Status:** `getStatus()`

### Trainer System (Strategy Pattern)

**Trainer interface** defines custom log handlers with `pack` (categorization) and `volume` (filtering).

**Platform-Specific Trainers:**

*Android:*
- `AndroidLogTrainer` - Logcat output (SYSTEM)
- `AndroidTestLogTrainer` - Logcat for tests (SYSTEM)
- `UnitTestTrainer` - Console output (CONSOLE)
- `ColoredUnitTestTrainer` - ANSI colored console (CONSOLE)

*iOS:*
- `NSLogTrainer` - NSLog system logging (SYSTEM)
- `UnitTestTrainer` - Console with timestamps (CONSOLE)
- `ColoredUnitTestTrainer` - ANSI colored with auto-detection (CONSOLE)

**Pack Enum:** CONSOLE, SYSTEM, FILE, CUSTOM. Prevents duplicates except CUSTOM.

### Auto-Detection Features

**Tag Detection (expect/actual):**
- Android: Stack trace parsing via `Thread.currentThread().stackTrace`
- iOS: Symbol parsing for Swift mangled names + Kotlin symbols via `backtrace()`
- Android: 23-char tag limit enforced
- iOS: Auto-detection disabled by default (performance)
- Config: `BarkConfig.autoTagDisabled` (iOS only)

**Test Detection:**
- Android: Detects JUnit, Robolectric, Espresso, AndroidX Test
- iOS: Detects XCTest framework
- Automatically switches between system and console output
- No build-time configuration needed

**Color Support Detection (iOS):**
- Detects ANSI support via `isatty()` and `TERM` env var
- Works in Terminal/CI, not in Xcode test console
- Location: `shared/src/iosMain/.../detectors/ColorSupportDetector.kt`

---

## Common Tasks

### Creating a Custom Trainer
1. Implement `Trainer` interface with `pack` and `volume`
2. Add tests in appropriate platform test folder
3. Document in README.md

Reference: `AndroidLogTrainer.kt` or `NSLogTrainer.kt`

### Modifying Tag Detection
- Android: `shared/src/androidMain/.../detectors/TagDetection.kt`
- iOS: `shared/src/iosMain/.../detectors/TagDetection.kt`
- Tests: Check corresponding `TagDetectionTest.kt` files

### Adding Platform-Specific Features
Use expect/actual pattern:
1. Declare `expect` in `commonMain`
2. Implement `actual` in `androidMain` and `iosMain`

### Running Tests
```bash
./gradlew test                          # All tests
./gradlew :shared:test                  # Library only
./gradlew :shared:testDebugUnitTest     # Android
./gradlew shared:iosSimulatorArm64Test  # iOS (Kotlin)
./gradlew shared:koverXmlReport         # Coverage
```

**Coverage:** 90% minimum enforced by Kover.

---

## Development Workflow

### Git Flow
- `main` - Production releases (protected)
- `feature/*` - Feature development
- `release/*` - Release preparation
- `chore/*` - Maintenance

### Making Changes
1. Create feature branch from `main`
2. Make changes in `shared/src/`
3. Write tests (90% coverage required)
4. Update README.md if API changes
5. Run `./gradlew build test`
6. Use conventional commits
7. Create PR to `main`

### Release Process (Maintainer Only)
```bash
./tools/release-process.sh <version>  # Manual
git tag v0.2.0 && git push origin v0.2.0  # Automated (CI/CD)
```

CI/CD: `.github/workflows/prep-release.yml` publishes to Maven Central

---

## Important Files

### Library Core
- `shared/src/commonMain/.../Bark.kt` - Main API
- `shared/src/commonMain/.../Trainer.kt` - Handler interface
- `shared/src/commonMain/.../Level.kt` - Log levels
- `shared/src/commonMain/.../Pack.kt` - Trainer categories

### Platform Implementations
- `shared/src/androidMain/.../trainers/` - 4 Android trainers
- `shared/src/iosMain/.../trainers/` - 3 iOS trainers
- `shared/src/androidMain/.../detectors/` - Tag + test detection
- `shared/src/iosMain/.../detectors/` - Tag + test + color detection

### Build & Config
- `shared/build.gradle.kts` - **Library build & Maven publishing**
- `build.gradle.kts` - Root build
- `gradle/libs.versions.toml` - Version catalog

### Tests
- `shared/src/commonTest/` - Platform-agnostic tests
- `shared/src/androidUnitTest/` - 18 Android test files
- `shared/src/iosTest/` - 6 iOS test files (Kotlin)
- `sample-ios/barK-sampleTests/` - 3 Swift test files

### Sample Apps
- `sample-android/` - Jetpack Compose app with repository pattern
- `sample-ios/barK-sample/` - SwiftUI app mirroring Android structure
- Both use purple branding (#7f51ff) and demonstrate realistic usage

### Documentation
- `README.md` - User-facing docs (comprehensive)
- `CONTRIBUTING.md` - Development guide
- `ios/README.md` - iOS integration guide
- `CLAUDE.md` - This file

---

## Technical Details

### Build System
- Gradle 8.10.1 with Kotlin DSL
- Kotlin 2.1.21 (backward compatible to 1.9+)
- Android: compileSdk 35, minSdk 24, Java 11
- iOS: iosX64, iosArm64, iosSimulatorArm64

### Dependencies (Minimal)
- Library: Only `kotlin-test` in commonMain
- Testing: JUnit 4, Kotlin Test, Kotlinx Coroutines Test
- Coverage: Kover (90% minimum)

### Publishing
- Maven Central via Sonatype OSSRH
- Group: `com.ivangarzab`
- Artifacts: `bark` (common), `bark-android`
- GPG signing via CI

---

## Platform-Specific Notes

### Android
- **Trainers:** Logcat (AndroidLogTrainer), console (UnitTestTrainer), colored (ColoredUnitTestTrainer)
- **Tag Detection:** Stack trace parsing with 23-char limit
- **Test Detection:** JUnit, Robolectric, Espresso, AndroidX Test
- **Auto-switching:** Test trainers activate automatically in test environments

### iOS
- **Trainers:** NSLog (NSLogTrainer), console (UnitTestTrainer), colored (ColoredUnitTestTrainer)
- **Tag Detection:** Swift symbol demangling via `backtrace()`, disabled by default (performance)
- **Test Detection:** XCTest framework inspection
- **Color Support:** Auto-detected via `isatty()` and `TERM`, works in Terminal/CI, not Xcode console
- **Swift API:** BarkExtensions.swift in `/ios` directory (user-copied, not published)
- **Config:** `BarkConfig.autoTagDisabled = false` to enable auto-tag detection
- **Framework:** Static framework only
- **CI Testing:** iPhone 15 simulator via Xcode

**Key Differences:**
- iOS uses `NSDateFormatter` vs Android's `SimpleDateFormat`
- iOS error prefix: "Error:" vs Android: "Exception:"
- iOS requires copying BarkExtensions.swift for clean API
- Both platforms have full test environment detection

---

## Tips & Gotchas

### General
- Always read files before editing
- Prefer editing over creating new files
- Use expect/actual pattern for platform-specific code
- Maintain 90% test coverage
- Support Kotlin 1.9+ (backward compatibility)

### Common Pitfalls
- Sample app changes don't affect library (they're in separate modules)
- Library code is in `shared/`, not root
- iOS BarkExtensions.swift is NOT part of published framework
- Can't have multiple trainers with same Pack (except CUSTOM)
- Xcode console doesn't support ANSI colors
- iOS auto-tag has performance cost

### Performance
- iOS auto-tag uses C interop - expensive, disabled by default
- `muzzle()` and volume checks return early
- Use direct log methods for optimal performance

---

## Testing Strategy

### CI/CD Pipeline
**Workflow:** `.github/workflows/unit-tests.yml`
- Parallel jobs: kotlin-tests (Ubuntu) + ios-tests (macOS)
- Kotlin: All Gradle tests + Kover coverage
- iOS: Kotlin tests + Xcode tests (iPhone 15 simulator)
- Unified test summary comments on PRs
- Artifacts retained 30 days

### Coverage
- Minimum: 90% (Kover enforced)
- Reports: `shared/build/reports/kover/`

---

## Design Patterns

1. **Singleton** - Bark object as API entry
2. **Strategy** - Trainer interface for handlers
3. **Expect/Actual** - Platform implementations
4. **Enum-Based Categorization** - Pack and Level
5. **Repository** - Sample apps demonstrate

---

## Quick Commands

```bash
./gradlew build                         # Build all
./gradlew :shared:test                  # Test library
./gradlew shared:iosSimulatorArm64Test  # iOS tests
./gradlew shared:koverXmlReport         # Coverage
./gradlew :sample-android:installDebug  # Run Android sample
./gradlew publishToMavenLocal           # Local Maven
```

---

## Notes for Claude

### When Adding Features
1. Check if platform-specific (expect/actual)
2. Consider Kotlin 1.9+ compatibility
3. Maintain 90% coverage
4. Update README.md for API changes
5. Consider performance (especially iOS auto-detection)

### When Fixing Bugs
1. Check existing tests first
2. Look at both platform implementations
3. Verify fix on both platforms if common code
4. Add regression test
5. Update sample apps if needed

### When Working with iOS
- Keep BarkExtensions.swift in sync (`/ios` + sample app)
- iOS trainers mirror Android but use NSDateFormatter and "Error:" prefix
- Test via Gradle AND Xcode (both in CI)
- Color detection works differently (isatty vs runtime checks)

### Architecture Questions
- Refer to "Core Architecture" section
- Point to specific file locations
- Explain expect/actual for platform code

---

**This is a living document. Update when significant changes occur.**
