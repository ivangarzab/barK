[![Maven Central](https://img.shields.io/maven-central/v/com.ivangarzab/bark.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.ivangarzab/bark)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-orange?logo=kotlin)
![Android](https://img.shields.io/badge/Android-✔️-green?logo=android)
![iOS](https://img.shields.io/badge/iOS-✔️-lightgrey?logo=apple)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/Heapy/awesome-kotlin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Check](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml/badge.svg?branch=main)](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml)

![wordmark](/assets/wordmark.png)

***barK**: A simple, extensible logging library for Kotlin Multiplatform with automatic tag detection and support for test runs*

```diff
- NOTICE: barK has officially moved into Maven Central!

+ Starting with v0.1.0, barK has migrated out of Jitpack, and is now hosted in Maven Central.
+ If you're using a version of barK v0.0.x, feel free to drop the Jitpack repository if you're not using it elsewhere.
```

## Why barK?

barK solves common logging pain points with a **memorable, themed API** and **powerful automation**:

- 🏷️ **Automatic tag detection** - No more manual TAG constants
- 🧪 **Smart test detection** - Different output for tests vs. production
- 🎯 **Trainer system** - Flexible, extensible output destinations
- 🔄 **Kotlin Multiplatform** - Full Android + iOS support

_**Born from real SDK development needs**_ - when you need different logging behavior for Android runs vs. test runs, **barK** has you covered.

### As Seen On

**Medium:**
- *[barK: A Lightweight Logging Library for Android](https://levelup.gitconnected.com/bark-a-lightweight-logging-library-for-android-14583711bc04)*
- *iOS platform support article coming soon!*

---

## Installation

Add barK to your Kotlin Multiplatform project via Maven Central:

**Kotlin DSL:**
```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.ivangarzab:bark:<version>")
        }
    }
}
```

**Groovy:**
```groovy
// build.gradle
repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation 'com.ivangarzab:bark:<version>'
            }
        }
    }
}
```

---

## Quick Start

### Android

#### Initialize (One-time setup)

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Train barK with Android logging
        if (BuildConfig.DEBUG) {
            Bark.train(AndroidLogTrainer())
        }

        // Optionally add test trainer (auto-activates during tests)
        Bark.train(ColoredUnitTestTrainer())
    }
}
```

#### Log anywhere

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Automatic tag detection - shows [MainActivity] in logs
        Bark.d("App started successfully")
        Bark.i("User logged in: ${user.name}")
        Bark.e("Network error", exception)
    }
}
```

**That's it!** barK automatically:
- ✅ Detects your class names as tags
- ✅ Switches to colored console output during tests
- ✅ Uses Android Logcat for production

---

### iOS

#### Import the framework

```swift
import shared  // Your KMP shared module name
```

#### Copy BarkExtensions.swift (Optional but Recommended)

For a cleaner Swift API, copy `ios/BarkExtensions.swift` into your iOS app target. This transforms:

```swift
Bark.shared.d(message: "Hello")  // Without extensions
Bark.d("Hello")                   // With extensions ✨
```

#### Initialize (One-time setup)

```swift
@main
struct MyApp: App {
    init() {
        // Enable auto-tag detection (optional, has performance cost)
        BarkConfig.shared.autoTagDisabled = false

        // Train barK with iOS logging
        #if DEBUG
        Bark.train(trainer: NSLogTrainer())
        Bark.train(trainer: ColoredUnitTestTrainer())
        #else
        Bark.train(trainer: NSLogTrainer(volume: .warning))
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

#### Log anywhere

```swift
class UserRepository {
    func saveUser(user: User) {
        // Automatic tag detection - shows [UserRepository] in logs
        Bark.d("Saving user: \(user.name)")
        Bark.i("User saved successfully")
    }
}
```

**That's it!** barK automatically:
- ✅ Detects your class names as tags (when enabled)
- ✅ Switches to colored console output during tests
- ✅ Uses NSLog for system logging

**iOS-Specific Notes:**
- Auto-tag detection is **disabled by default** (performance cost). Enable with `BarkConfig.shared.autoTagDisabled = false`
- Color support works in Terminal and CI/CD, but not in Xcode console
- See [ios/README.md](ios/README.md) for detailed iOS integration guide

---

## Features

### 🏷️ Automatic Tag Detection

No more manual TAG constants! barK automatically detects the calling class:

```kotlin
class UserRepository {
    fun saveUser(user: User) {
        Bark.i("Saving user: ${user.name}")  // Tag: [UserRepository]
    }
}

class MainActivity : AppCompatActivity() {
    fun onCreate() {
        Bark.d("Activity created")  // Tag: [MainActivity]
    }
}
```

### 🧪 Smart Test Detection

Different output for different environments:

```kotlin
// In your app: Shows in system logs (Logcat/NSLog)
Bark.d("User action performed")

// In unit tests: Shows colored in console
@Test
fun testUserAction() {
    Bark.d("Testing user action")  // Colored console output!
}
```

Works on both Android and iOS with automatic detection of JUnit, XCTest, and other testing frameworks.

### 🎯 Flexible Trainer System

Easy to extend and customize:

```kotlin
// Basic setup
Bark.train(AndroidLogTrainer())

// Multiple outputs
Bark.train(AndroidLogTrainer())           // System Logcat
Bark.train(ColoredUnitTestTrainer())      // Colored console output
Bark.train(FileTrainer("app.log"))        // File logging
Bark.train(CrashReportingTrainer())       // Custom trainers

// Custom volume control per trainer
Bark.train(AndroidLogTrainer(volume = Level.DEBUG))    // All levels
Bark.train(FileTrainer(volume = Level.ERROR))          // Errors only
```

**Platform Parity:**
- Android: `AndroidLogTrainer`, `AndroidTestLogTrainer`, `UnitTestTrainer`, `ColoredUnitTestTrainer`
- iOS: `NSLogTrainer`, `UnitTestTrainer`, `ColoredUnitTestTrainer`

---

## Sample Apps

Check out the included sample apps to see barK in action:

**Android:**
```bash
./gradlew :sample-android:installDebug
```

**iOS:**
Open `sample-ios/barK-sample/barK-sample.xcodeproj` in Xcode and run.

Both samples demonstrate:
- 📱 **Realistic usage** with repository pattern
- 🔧 **Multiple trainers** and configuration
- 🏷️ **Auto-tag detection** across multiple classes
- 🎯 **Trainer switching** between environments
- 🧪 **Unit test suites** showcasing console features

---

## Kotlin Multiplatform

barK is built for Kotlin Multiplatform from the ground up with full platform parity:

```kotlin
// ✅ Kotlin Multiplatform - Shared API
// ✅ Android - Logcat, test detection, colored console
// ✅ iOS - NSLog, XCTest detection, colored console (Terminal/CI)
```

**Common API, platform-specific implementations:**
- Automatic tag detection works on both platforms (with platform-specific stack trace parsing)
- Test environment detection on Android (JUnit, Robolectric, Espresso) and iOS (XCTest)
- Colored console output with automatic color support detection

---

## Documentation

- **[Advanced Usage Guide](docs/ADVANCED_USAGE.md)** - Custom trainers, volume control, runtime management, platform-specific notes
- **[SDK Integration Guide](docs/SDK_INTEGRATION.md)** - Using barK in SDKs, integrator control, best practices
- **[iOS Integration Guide](ios/README.md)** - Detailed iOS setup, BarkExtensions.swift, platform-specific configuration
- **[Contributing Guide](CONTRIBUTING.md)** - Development setup, coding guidelines, PR process
- **[Changelog](CHANGELOG.md)** - All notable changes per release, following [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and [Semantic Versioning](https://semver.org/spec/v2.0.0.html)

---

## Comparison

### vs. Android Log

| Feature | barK | Android Log |
|---------|------|-------------|
| Manual TAG constants | ❌ | ✅ |
| Test environment handling | ✅ | ❌ |
| Multiple outputs | ✅ | ❌ |
| Runtime control | ✅ | ❌ |
| Kotlin Multiplatform | ✅ | ❌ |

### vs. Timber

| Feature | barK | Timber |
|---------|------|--------|
| Auto-tag detection | ✅ | ✅ |
| Test environment detection | ✅ | ❌ |
| Kotlin Multiplatform | ✅ | ❌ |
| Multiple trainers | ✅ | ✅ |
| Runtime muzzling | ✅ | ❌ |

---

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## License

```
Copyright 2025 Ivan Garza

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

**Made with 🖤 for the Kotlin community**

***barK**: Because every log deserves a good home* 🐕🏠