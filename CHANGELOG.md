# Changelog

All notable changes to barK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.2.1] - 2026-04-04

### CI/CD
- Update the prep-release.yml workflow to make up for the dual namespace collision

### Documentation
- Implement CHANGELOG automation (#31)

### Maintenance
- Clean up docs for iOS installation (#29)
## [0.2.0] - 2026-01-27

### Added
- Feat(ios): Create the same classes present on the Android sample app for the iOS side
-  Delete the ContentView.swift class
-  Update the App object so that it initializes barK on app start, and discriminate between DEBUG & RELEASE builds
- Feat(ios): Create new UnitTest Trainers to emulate Android and pursue parity
- Small update on Android's UnitTestTrainer class
- Feat(ios): Create ColoredUnitTestTrainer to attempt to deduce if the current test runner can print colors or not, and implement into the ColoredUnitTestTrainer.kt
-  Update docs for both iOS UnitTestTrainers
- Feat(ios): Create public and copyable BarkExtensions.swift file in new /ios directory for integrators to get cleaner API by copying this file over
-  Update BarkExtensions.swift already present inside the :sample-ios

### Design
- Add Android app icon
- Design(ios): Add iOS app icon & accent color of #7f51ff
-  Add new ic_ios.png image file for the iOS app icon

### Documentation
- Update docs and prep for next release (#28)

### Maintenance
- Re-organize all Xcode files into directories simulating the Android hierarchy
- Clean up for PR
- Update all usage of Bark.shared to the simplified form after properly integrating the distributable extensions

### Testing
- Create test classes for all new UnitTest Trainers, and their own utility classes
- Create unit tests for the :sample-ios module similar to the ones found in :sample-android
- Update unit-tests.yml to run Xcode tests as well
## [0.1.3] - 2025-11-09

### Fixed
- Fix prep-release.yml missed typo
## [0.1.2] - 2025-11-09

### Added
- Implement new BarkExtensions.swift file for a cleaner API in iOS
- Correctly implement tag auto-detect feature in iOS, but add a disabled flag and make it optional
- Feat: Bring in kotlin-kover, and use to grab test coverage for both Kotlin + iOS
- Get rid of Jacoco
- Add iOS test coverage and reporting into the unit-test.yml workflow

### Changed
- Start refactoring unit-tests.yml for the upcoming iOS support
- ContentView.swift to showcase barK working on iOS

### Documentation
- Update README.md to reflect that iOS is being worked on

### Fixed
- Fix iOS' TagDetection.kt actual implementation to properly capture the class name for auto-tag
- Do not fail test jobs when a test fails or errors out
- Attempt to fix unit-tests.yml iOS run
- Attempt to fix the final publication step, again, by manually fetching artifacts and pushing them to Maven Central Portal
- Fix name conventions errors
- Move the publish-finalizer.sh class to the correct dir level, and grant

### Maintenance
- Update Kotlin version for better KMP support with Xcode
- Update kover version and expected coverage to look for
- Clean up deprecated flag
- Create two new Run Configurations, and refactor the existing one for better granularity on easy unit test runs

### Testing
- Fix and enhance the TagDetectionTest class
- Fix the single Android instrumented test by making sure we use the androidx test runner into the :sample-android module
## [0.1.1] - 2025-11-08

### Changed
- Consolidate publication steps amids a desynchronization error found on the first run
- Update barK version for example Android app

### Documentation
- Add migration notice for barK to Maven Central

### Fixed
- Switch out release process order to attempt to avoid rebase merges
## [0.1.0] - 2025-11-08

### Added
- Feat: Rework the :shared module's Gradle build file to switch publications from Jitpack to Maven Central Portal
-  Setup manual signing credentials
-  Change repository target URL
-  Rename all shared artifacts to bark
- Implement new getPropertyOrEnv() Gradle function in order to properly get either a Gradle Property, or an Environment Variable

### Changed
- Update both GitHub Action workflow & local release process with Maven Central as the new target
- Update :sample-android module's example usage after migrating to Maven Central
- README.md a bit more

### Documentation
- Update all docs to reflect the migration from jitpack to maven central

### Maintenance
- Delete jitpack.yml as it won't be needed anymore
## [0.0.10] - 2025-11-07

### Fixed
- Fix the :shared module's build.gradle.kts so that it publishes ALL artifacts correctly for KMP
## [0.0.1] - 2025-08-13
[0.2.1]: https://github.com/ivangarzab/barK/compare/0.2.0...0.2.1
[0.2.0]: https://github.com/ivangarzab/barK/compare/0.1.3...0.2.0
[0.1.3]: https://github.com/ivangarzab/barK/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/ivangarzab/barK/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/ivangarzab/barK/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/ivangarzab/barK/compare/0.0.10...0.1.0
[0.0.10]: https://github.com/ivangarzab/barK/compare/0.0.9...0.0.10
[0.0.1]: https://github.com/ivangarzab/barK/releases/tag/0.0.1

