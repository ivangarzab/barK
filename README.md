# barK

## Core Architecture

**`expect/actual` pattern** for KMP platform implementations

**Common interface** in `commonMain/kotlin/Logger.kt`

**Platform-specific implementations** in `androidMain/` and `iosMain/`

## Logging Methods

**Production**: Android uses `Log.d()`, iOS uses `println()`

**Testing**: All platforms use `println()` for test output visibility

**Automatic tag detection** via stack trace analysis

**Manual tag override** still supported

## Test Detection

**Android**: `BuildConfig.IS_TESTING` (build-time detection)

**iOS**: Custom interface or runtime detection

**Cross-platform**: `println()` for all test environments

## Configuration

**Log levels**: VERBOSE, DEBUG, INFO, WARNING, ERROR

**Release builds**: Build-time flag to disable logging entirely

**Global log level**: Configurable via `BarK.minLogLevel`

## Features

**Color-coded output** for test environments (red errors, etc.)

**Timestamp formatting** for all log messages

**Exception handling** with colored error messages

**Tag auto-detection** (Timber-like functionality)

## Usage

``` kt
BarK.init(
  minLogLevel = Level.DEBUG,
  logRelease = false,
  logTest = false,
  colorTest = false
)

// Automatic tag detection
BarK.d("message")  // Tag: "ComplyApiService"

// Manual tag override
BarK.d("message", tag = "CustomTag")

// Test vs Production
// Tests: println() with colors
// Production: Platform-specific logging
```

## Build Configuration

``` kt
// build.gradle.kts
buildConfigField("boolean", "IS_TESTING", isTestBuild.toString())
buildConfigField("boolean", "ENABLE_LOGGING", "true/false")
```

## Platform Support

**Android**: `android.util.Log` for production, `println()` for tests

**iOS**: `println()` for both (no Android Log available)

**JVM**: `println()` for both (no Android Log available)

**All platforms**: Same API, different underlying implementation
