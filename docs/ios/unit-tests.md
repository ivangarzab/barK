# iOS Unit Tests

**barK** automatically detects XCTest environments and switches to console output — no configuration required.

---

## How Test Detection Works

**barK** inspects the loaded frameworks at runtime for `XCTest`. When detected, `ColoredUnitTestTrainer` activates automatically and `NSLogTrainer` output is suppressed.

This works for:

- XCTest unit tests (via `xctest` runner)
- Xcode UI tests
- CI runs (GitHub Actions, Fastlane, etc.)

---

## Recommended Test Setup

```swift title="ExampleTests.swift"
import XCTest
@testable import shared

final class ExampleTests: XCTestCase {

    override func setUp() {
        super.setUp()
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer())
    }

    override func tearDown() {
        Bark.releaseAllTrainers()
        super.tearDown()
    }

    func testSomething() {
        Bark.d("Starting test")
        // ...
        Bark.i("Test completed")
    }
}
```

---

## Choosing a Test Trainer

### `ColoredUnitTestTrainer`

ANSI-colored output. Works in Terminal and CI (GitHub Actions, Fastlane). Falls back to plain text automatically in Xcode's test console, which doesn't support ANSI.

```
[D] MyClass: Debug message       ← Terminal / CI
[I] MyClass: Info message
```

### `UnitTestTrainer`

Plain text with timestamps. Always readable regardless of environment — safe choice for Xcode console:

```
12:34:56 [D] MyClass: Debug message
12:34:56 [I] MyClass: Info message
```

!!! note "Xcode console limitation"
    Xcode's test output panel strips ANSI escape codes. If you're running tests exclusively in Xcode (not from the command line), `UnitTestTrainer` gives cleaner output.

---

## Color Support Detection

`ColoredUnitTestTrainer` auto-detects ANSI support using two checks:

1. `isatty()` — is stdout connected to a terminal?
2. `TERM` environment variable — is it set to a color-supporting value?

| Environment | Colors |
|-------------|--------|
| Terminal (macOS) | ✅ |
| GitHub Actions | ✅ |
| Fastlane | ✅ |
| Xcode test console | ❌ (falls back to plain text) |

---

## Log Level Filtering in Tests

```swift
override func setUp() {
    super.setUp()
    Bark.releaseAllTrainers()
    // Only show warnings and above during this test class
    Bark.train(trainer: ColoredUnitTestTrainer(minLevel: .warning))
}
```

---

## Running Tests from the Command Line

**barK**'s Kotlin tests on iOS run via Gradle:

```bash
./gradlew shared:iosSimulatorArm64Test
```

Colors will render correctly in terminal output. Xcode tests can also be run directly:

```bash
xcodebuild test \
  -project sample-ios/barK-sample/barK-sample.xcodeproj \
  -scheme barK-sample \
  -destination 'platform=iOS Simulator,name=iPhone 15'
```
