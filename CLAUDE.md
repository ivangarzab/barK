# CLAUDE.md - barK Project Guide

**Last updated:** 2026-01-27
**Version:** 0.1.3
**Purpose:** Quick reference for Claude Code when working with the barK logging library

---

## Project Overview

**barK** is a lightweight, extensible logging library for Kotlin Multiplatform with automatic tag detection and smart test environment handling. Published on Maven Central as `com.ivangarzab:bark`.

**Key Features:**
- Cross-platform support (Android + iOS)
- Automatic tag detection from stack traces
- Smart test environment detection
- Multiple concurrent log handlers ("Trainers")
- Runtime control (muzzle/unmuzzle)
- Dog-themed API (bark, train, muzzle, etc.)

**Philosophy:** SDK-friendly, minimal dependencies, extensible by design, developer-friendly API.

---

## Project Structure

```
barK/
├── shared/                    # Main KMP library (THIS IS THE LIBRARY)
│   ├── src/
│   │   ├── commonMain/       # Platform-agnostic API
│   │   ├── androidMain/      # Android-specific implementations
│   │   ├── iosMain/          # iOS-specific implementations
│   │   ├── commonTest/       # Shared tests
│   │   ├── androidUnitTest/  # Android-specific tests
│   │   └── iosTest/          # iOS-specific tests
│   └── build.gradle.kts      # Library build config (includes publishing)
├── sample-android/            # Android demo app (not published)
├── sample-ios/                # iOS demo app (not published)
├── tools/                     # Release automation scripts
└── .github/workflows/         # CI/CD pipelines
```

**Important:** The library code is in `shared/`, not at the root. Sample apps are demos only.

---

## Core Architecture

### The Bark Object (`shared/src/commonMain/.../Bark.kt`)

Singleton API entry point. Main methods:
- **Logging:** `v()`, `d()`, `i()`, `w()`, `e()` - Log at different levels
- **Trainer Management:** `train()`, `untrain()`, `releaseAllTrainers()`
- **Runtime Control:** `muzzle()`, `unmuzzle()` - Enable/disable logging
- **Tag Override:** `tag()`, `untag()` - Set global tag
- **Status:** `getStatus()` - Get current configuration

### Trainer System (Strategy Pattern)

**Trainer Interface:** Define custom log handlers
- `log(level, tag, message, throwable)` - Handle a log entry
- `volume` - Minimum log level to handle (filter)
- `pack` - Categorization (CONSOLE, SYSTEM, FILE, CUSTOM)

**Pack Enum:** Prevents duplicate trainers of the same category (except CUSTOM)

**Platform-Specific Trainers:**
- Android: `AndroidLogTrainer`, `AndroidTestLogTrainer`, `UnitTestTrainer`, `ColoredUnitTestTrainer`
- iOS: `NSLogTrainer`

**Key Pattern:** Single API, multiple output destinations via strategy pattern.

### Auto-Detection Features

**Tag Detection (expect/actual pattern):**
- Android: Parses `Thread.currentThread().stackTrace` to extract calling class
- iOS: Complex symbol parsing for Swift mangled names and Kotlin symbols
- Respects Android 23-char tag limit
- iOS auto-detection disabled by default (performance trade-off)

Location:
- Common: `shared/src/commonMain/.../detectors/TagDetection.kt` (expect)
- Android: `shared/src/androidMain/.../detectors/TagDetection.kt`
- iOS: `shared/src/iosMain/.../detectors/TagDetection.kt`

**Test Detection (Android-only):**
- Runtime stack trace inspection for JUnit, Robolectric, Espresso, AndroidX Test
- Automatically switches between Logcat and console output

Location: `shared/src/androidMain/.../detectors/TestDetection.kt`

---

## Common Tasks

### Adding a New Log Level

1. Edit `shared/src/commonMain/.../Level.kt` - Add enum entry
2. Add corresponding method to `Bark` object (e.g., `fun c(message, throwable)`)
3. Update tests in `commonTest/BarkTest.kt`
4. Update README.md examples

### Creating a Custom Trainer

1. Implement `Trainer` interface
2. Set appropriate `pack` and `volume`
3. Add tests in appropriate test folder
4. Document in README.md

Example locations to reference:
- `shared/src/androidMain/.../trainers/AndroidLogTrainer.kt`
- `shared/src/iosMain/.../trainers/NSLogTrainer.kt`

### Modifying Tag Detection Logic

**Android:** Edit `shared/src/androidMain/.../detectors/TagDetection.kt`
- `detectTag()` function parses stack traces
- Filters framework classes via `isFrameworkClass()`
- Respects 23-character limit

**iOS:** Edit `shared/src/iosMain/.../detectors/TagDetection.kt`
- Uses C interop (`backtrace()`) for stack traces
- Complex Swift name demangling logic
- Performance-sensitive (disabled by default)

**Tests:** Check `androidUnitTest/detectors/TagDetectionTest.kt` and `iosTest/detectors/TagDetectionTest.kt`

### Adding Platform-Specific Features

Use expect/actual pattern:
1. Declare `expect` in `commonMain`
2. Implement `actual` in `androidMain` and `iosMain`

Example: See `TagDetection.kt` implementations across platforms.

### Updating Dependencies

1. Edit `gradle/libs.versions.toml` - Version catalog
2. Update `shared/build.gradle.kts` or root `build.gradle.kts`
3. Sync Gradle
4. Run tests to verify compatibility

### Running Tests

```bash
# All tests
./gradlew test

# Library tests only
./gradlew :shared:test

# Android tests
./gradlew :shared:testDebugUnitTest

# iOS tests (requires macOS)
./gradlew shared:iosSimulatorArm64Test

# Coverage report
./gradlew shared:koverXmlReport shared:koverHtmlReport
```

**Coverage Requirement:** 90% minimum enforced by Kover.

---

## Development Workflow

### Git Flow Strategy

- `main` - Production releases (protected, use for PRs)
- `feature/*` - Feature development
- `release/*` - Release preparation
- `chore/*` - Maintenance tasks

### Making Changes

1. Create feature branch from `main`
2. Make changes in `shared/src/`
3. Write/update tests (90% coverage required)
4. Update README.md if API changes
5. Run `./gradlew build` and `./gradlew test`
6. Commit with conventional commits format
7. Create PR to `main`

### Release Process (Maintainer Only)

**Manual:**
```bash
./tools/release-process.sh <version>
```

**Automated (CI/CD):**
1. Push git tag: `git tag v0.1.4 && git push origin v0.1.4`
2. GitHub Actions publishes to Maven Central
3. Creates GitHub Release automatically

**Release Scripts:** Located in `tools/` directory
- `release-process.sh` - Main orchestrator
- `update-lib-version.sh` - Version bumping
- `publish-finalizer.sh` - Maven Central finalization

**CI/CD:** `.github/workflows/prep-release.yml`

---

## Important Files & Locations

### Library Core
- `shared/src/commonMain/kotlin/com/ivangarzab/bark/Bark.kt` - Main API
- `shared/src/commonMain/kotlin/com/ivangarzab/bark/Trainer.kt` - Handler interface
- `shared/src/commonMain/kotlin/com/ivangarzab/bark/Level.kt` - Log levels
- `shared/src/commonMain/kotlin/com/ivangarzab/bark/Pack.kt` - Trainer categories

### Platform Implementations
- `shared/src/androidMain/.../trainers/` - Android trainers
- `shared/src/iosMain/.../trainers/` - iOS trainers
- `shared/src/androidMain/.../detectors/` - Android detection logic
- `shared/src/iosMain/.../detectors/` - iOS detection logic

### Build & Configuration
- `shared/build.gradle.kts` - **Library build config & Maven publishing**
- `build.gradle.kts` - Root build file
- `settings.gradle.kts` - Module configuration
- `gradle/libs.versions.toml` - Version catalog
- `gradle.properties` - Gradle settings

### Tests
- `shared/src/commonTest/kotlin/BarkTest.kt` - Core API tests
- `shared/src/androidUnitTest/kotlin/` - Android-specific tests
- `shared/src/iosTest/kotlin/` - iOS-specific tests

### Documentation
- `README.md` - User-facing documentation (9,867 bytes, comprehensive)
- `CONTRIBUTING.md` - Development guide (6,885 bytes)
- `CLAUDE.md` - This file

### Sample Apps
- `sample-android/src/main/java/.../App.kt` - Android app entry point
- `sample-android/src/main/java/.../ui/SampleActivity.kt` - Main activity with Compose UI
- `sample-ios/barK-sample/` - iOS sample project (Xcode)

---

## Technical Details

### Build System
- **Gradle:** 8.10.1 with Kotlin DSL
- **Kotlin:** 2.1.21 (backward compatible to 1.9+)
- **Java Target:** 11
- **Android:** compileSdk 35, minSdk 24
- **iOS Targets:** iosX64, iosArm64, iosSimulatorArm64

### Dependencies (Minimal by Design)
- **Library:** Only `kotlin-test` in commonMain
- **Sample App:** Jetpack Compose, Material 3, AndroidX Core KTX
- **Testing:** JUnit 4, Kotlin Test, Kotlinx Coroutines Test
- **Coverage:** Kover (90% minimum)

### Publishing Configuration
- **Group ID:** `com.ivangarzab`
- **Artifact IDs:** `bark` (common), `bark-android` (Android)
- **Repository:** Maven Central via Sonatype OSSRH
- **Signing:** GPG with in-memory keys (CI only)

---

## Tips & Gotchas

### When Making Changes

1. **Always read before editing:** Never propose changes without reading the file first.
2. **Prefer editing over creating:** Always edit existing files rather than creating new ones unless absolutely necessary.
3. **Platform-specific code:** Use expect/actual pattern, not conditional compilation.
4. **Test coverage:** Maintain 90% coverage. Add tests for all new code.
5. **Android tag limit:** Keep tags under 23 characters (enforced in tag detection).
6. **iOS performance:** Auto-tag detection is expensive on iOS, disabled by default.
7. **Backwards compatibility:** Library supports Kotlin 1.9+, don't use newer-only features.

### Common Pitfalls

- **Modifying sample apps:** Changes to `sample-android/` or `sample-ios/` don't affect the library.
- **Wrong module:** Library code is in `shared/`, not root.
- **Publishing:** Only maintainers can publish. CI/CD handles releases via tags.
- **Test detection:** Only works on Android, not iOS.
- **Trainer duplicates:** Can't have multiple trainers with same Pack (except CUSTOM).

### Performance Considerations

- **iOS auto-tag:** Uses C interop (`backtrace()`) - heavy operation, disabled by default.
- **Early filtering:** `muzzle()` and volume checks return early to avoid string formatting.
- **Minimal allocations:** Use direct methods (`v`, `d`, `i`, etc.) for optimal performance.

### iOS-Specific Notes

- Static framework binaries generated for iOS
- NSLog integration via `NSLogTrainer`
- Auto-tag detection requires `BarkConfig.autoDetectTag = true`
- Swift interop tested in sample app
- Symbol parsing handles both Swift mangled names and Kotlin symbols

### Android-Specific Notes

- Logcat integration via `AndroidLogTrainer`
- Test detection works with JUnit, Robolectric, Espresso, AndroidX Test
- Automatic trainer switching in test environments
- Colored console output available via `ColoredUnitTestTrainer` (ANSI codes)

---

## Testing Strategy

### Test Organization

- **commonTest:** Platform-agnostic API tests
- **androidUnitTest:** Android trainer & detector tests
- **iosTest:** iOS trainer & detector tests
- **Sample app tests:** Real-world usage verification

### CI/CD Pipeline

**Workflow:** `.github/workflows/unit-tests.yml`
- Triggers: Push/PR to `main` or `develop`
- Parallel execution: Kotlin tests (Ubuntu) + iOS tests (macOS)
- Generates coverage reports (Kover)
- Publishes test results and coverage artifacts
- Comments results on PRs

### Coverage Requirements

- Minimum: 90% (enforced by Kover)
- Current: ~4,191 lines of code across ~23 files
- HTML reports: `shared/build/reports/kover/html/index.html`
- XML reports: `shared/build/reports/kover/report.xml`

---

## Design Patterns Used

1. **Singleton Pattern:** `Bark` object as global API entry point
2. **Strategy Pattern:** `Trainer` interface for pluggable handlers
3. **Expect/Actual Pattern:** Platform-specific implementations (KMP)
4. **Enum-Based Categorization:** `Pack` and `Level` enums
5. **Repository Pattern:** Sample app demonstrates with `UserRepository`
6. **Test Double Pattern:** Test trainers for verification

---

## Communication & Documentation

### README.md Structure

1. Badges (Maven Central, Kotlin MP, Android, iOS, License, Build)
2. Quick Start (installation & basic usage)
3. Features showcase
4. Advanced usage patterns
5. Comparison tables (vs Android Log, vs Timber)
6. Sample app information
7. Contributing & license

**When updating README:** Keep examples concise, show real use cases, maintain comparison tables.

### CONTRIBUTING.md Structure

1. Development setup
2. Git Flow strategy
3. Coding guidelines (Kotlin conventions)
4. Testing requirements (80%+ goal, 90% enforced)
5. PR process
6. Release process
7. Code of Conduct

---

## External Resources

- **Maven Central:** https://central.sonatype.com/artifact/com.ivangarzab/bark
- **GitHub:** https://github.com/ivangarzab/barK
- **Medium Article:** "barK: A Lightweight Logging Library for Android"
- **License:** Apache 2.0

---

## Quick Command Reference

```bash
# Build entire project
./gradlew build

# Build library only
./gradlew :shared:build

# Run all tests
./gradlew test

# Run library tests
./gradlew :shared:test

# Android tests
./gradlew :shared:testDebugUnitTest

# iOS tests (macOS only)
./gradlew shared:iosSimulatorArm64Test

# Generate coverage reports
./gradlew shared:koverXmlReport shared:koverHtmlReport

# Run sample app
./gradlew :sample-android:installDebug

# Publish to local Maven (testing)
./gradlew publishToMavenLocal

# Clean build
./gradlew clean build

# Check for dependency updates
./gradlew dependencyUpdates
```

---

## Project Status

- **Current Version:** 0.1.3
- **Published:** Maven Central
- **Android Support:** Stable (API 24+)
- **iOS Support:** Stable (static framework)
- **Active Development:** Yes
- **Latest Activity:** Documentation updates on `chore/updating-docs` branch

---

## Notes for Claude

### When Asked to Add Features

1. Check if it requires platform-specific code (expect/actual)
2. Consider backwards compatibility (Kotlin 1.9+)
3. Update tests for 90% coverage
4. Update README.md examples if API changes
5. Check iOS performance implications for auto-detection features
6. Verify against Android tag length limits if tag-related

### When Asked to Fix Bugs

1. Check existing tests first - might already have coverage
2. Look for similar patterns in other platform implementations
3. Verify fix works on both Android and iOS (if common code)
4. Add regression test
5. Check if sample apps need updates

### When Asked About Architecture

- Refer to "Core Architecture" section above
- Point to specific file locations
- Explain expect/actual pattern for platform code
- Reference design patterns section

### When Refactoring

- Maintain public API backwards compatibility
- Keep minimal dependency philosophy
- Preserve performance characteristics (especially iOS auto-tag)
- Update KDoc comments if signature changes
- Run full test suite across platforms

---

**This document is a living guide. Update it when significant architectural changes occur.**
