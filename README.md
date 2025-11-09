[![Build Check](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml/badge.svg?branch=main)](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.ivangarzab/bark.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.ivangarzab/bark)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Kotlin](https://img.shields.io/badge/kotlin-android-green.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/Heapy/awesome-kotlin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

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
- 🔄 **Kotlin Multiplatform** - Works across Android + iOS

_**Born from real SDK development needs**_ - when you need different logging behavior for Android runs vs. test runs, **barK** has you covered.

### As Seen On

**Medium:** *[barK: A Lightweight Logging Library for Android](https://levelup.gitconnected.com/bark-a-lightweight-logging-library-for-android-14583711bc04)*

---

## Quick Start

### 1. Add Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.ivangarzab:bark:<version>")
}
```

### 2. Initialize (One-time setup)

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Train barK with Android logging
        if (BuildConfig.DEBUG) {
            Bark.train(AndroidLogTrainer())
        }
        
        // Optionally add test trainer (auto-activates during tests)
        Bark.train(ColoredTestTrainer())
    }
}
```

### 3. Log anywhere

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
// In your app: Shows in Android Logcat
Bark.d("User action performed")

// In unit tests: Shows colored in IDE console  
@Test
fun testUserAction() {
    Bark.d("Testing user action")  // Colored console output!
}
```

### 🎯 Flexible Trainer System

Easy to extend and customize:

```kotlin
// Basic setup
Bark.train(AndroidLogTrainer())

// Multiple outputs
Bark.train(AndroidLogTrainer())           // Regular Logcat
Bark.train(UnitTestTrainer())             // Colored console output using print()
Bark.train(FileTrainer("app.log"))        // File logging  
Bark.train(CrashReportingTrainer())       // Custom trainers

// Custom volume control per trainer
Bark.train(AndroidLogTrainer(volume = Level.DEBUG))    // All levels
Bark.train(UnitTestTrainer(volume = Level.ERROR))      // Errors only
```

---

## Advanced Usage

### Custom Trainers

Create your own output destinations:

```kotlin
// Custom error capturing trainer for warnings, crashing and other all errors
class CustomErrorTrainer(
    override val volume: Level = Level.WARNING,
) : Trainer {
    override val pack: Pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal >= volume.ordinal) {
            // Handle error
        }
    }
}

// Custom trainer to send Slack messages
class SlackTrainer(
    override val volume: Level = Level.INFO,
    private val webhookUrl: String
) : Trainer {
    override val pack: Pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal >= volume.ordinal) {
            // Send to Slack
        }
    }
}
```

### Volume Control

Different trainers can have different volume thresholds:

```kotlin
Bark.train(AndroidLogTrainer(volume = Level.DEBUG))     // Everything to Logcat
Bark.train(FileTrainer(volume = Level.WARNING))         // Warnings+ to file  
Bark.train(CrashReportingTrainer())                     // Errors only (internal logic)
```

### Global vs Auto-Detection Tags

```kotlin
// Auto-detection (default)
class PaymentService {
    fun processPayment() {
        Bark.d("Processing payment")  // Tag: [PaymentService]
    }
}

// Global tag override
Bark.tag("PAYMENT_SDK")
Bark.d("Payment processed")  // Tag: [PAYMENT_SDK]

// Back to auto-detection
Bark.untag()
Bark.d("Payment completed")  // Tag: [PaymentService]
```

### Runtime Control

```kotlin
// Disable all logging
Bark.muzzle()
Bark.d("This won't appear anywhere")

// Re-enable  
Bark.unmuzzle()
Bark.d("This will appear")

// Check current status
println(Bark.getStatus())
// Output:
// Bark Status:
//   Muzzled: false
//   Tag: auto-detect  
//   Trainers: 2
//     [0] AndroidLogTrainer
//     [1] ColoredUnitTestTrainer
```

---

## SDK Integration

Perfect for SDK developers who need different logging behavior in different contexts:

```kotlin
class MySDK {
    fun initialize() {
        // Smart trainer setup
        if (BuildConfig.DEBUG) {
            Bark.train(AndroidLogTrainer(volume = Level.DEBUG))
            Bark.train(ColoredTestTrainer(volume = Level.DEBUG))
        } else {
            // Production: only errors, and only to crash reporting with a global tag
            Bark.train(CrashReportingTrainer())
            Bark.tag("MySDK")
        }
        
        Bark.i("SDK initialized")
    }
    
    fun performOperation() {
        Bark.d("Starting operation")
        try {
            // Your SDK logic
            Bark.i("Operation completed successfully")
        } catch (e: Exception) {
            Bark.e("Operation failed", e)
            throw e
        }
    }
}
```

SDK integrators can easily muzzle the barK instance, or retrain it with their own trainers:

```kotlin
// Muzzle SDK from integrating client side
Bark.muzzle()

// Or retrain for client's needs
Bark.train(AndroidLogTrainer(volume = System.Logger.Level.WARNING))
```

---

## Sample App

Check out the included sample app to see barK in action:

```bash
./gradlew :sample-android:installDebug
```

The sample demonstrates:
- 📱 **Realistic usage** in a typical Android app
- 🔧 **Debug console** for testing all features
- 🏷️ **Auto-tag detection** across multiple classes
- 🎯 **Trainer switching** and configuration
- 🧪 **Unit Test suite** to showcase console print features

---

## Kotlin Multiplatform

barK is built for Kotlin Multiplatform from the ground up:

```kotlin
// Platform implementations
// ✅ Kotlin Multiplatform (brand new!)
// ✅ Android (available now)
// 🏗️ iOS (working on it...)
```

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
// dependencyResolutionManagement
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ivangarzab:bark:<version>")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.ivangarzab:bark:<version>'
}
```

---

## Comparison

### vs. Android Log

| Feature | barK | Android Log |
|---------|------|-------------|
| Manual TAG constants | ❌ | ✅ |
| Test environment handling | ✅ | ❌ |
| Multiple outputs | ✅ | ❌ |
| Runtime control | ✅ | ❌ |
| String formatting | ✅ | ❌ |

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
