[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

![wordmark](/assets/wordmark.png)

***barK**: A simple, extensible logging library for Kotlin Multiplatform with automatic tag detection and themed API*

---

## Why barK?

barK solves common logging pain points with a **memorable, themed API** and **powerful automation**:

- 🏷️ **Automatic tag detection** - No more manual TAG constants
- 🧪 **Smart test detection** - Different output for tests vs. production
- 🎯 **Trainer system** - Flexible, extensible output destinations
- 🐕 **Themed API** - `train()`, `muzzle()`, `tag()` - easy to remember
- 🔄 **Kotlin Multiplatform** - Works across Android, iOS, JVM, and more

**Born from real SDK development needs** - when you need different logging behavior for Android runs vs. test runs, barK has you covered.

---

## Quick Start

### 1. Add Dependency

```kotlin
// build.gradle.kts (Module: app)
dependencies {
    implementation("com.ivangarzab:bark:1.0.0")
}
```

### 2. Initialize (One-time setup)

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Train barK with Android logging
        Bark.train(AndroidLogTrainer())
        
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
Bark.train(AndroidLogTrainer())           // Logcat
Bark.train(FileTrainer("app.log"))        // File logging  
Bark.train(CrashReportingTrainer())       // Crash service

// Custom volume control per trainer
Bark.train(AndroidLogTrainer(volume = Level.DEBUG))    // All levels
Bark.train(FileTrainer(volume = Level.ERROR))          // Errors only
```

### 🐕 Memorable Themed API

```kotlin
// Train different behaviors
Bark.train(AndroidLogTrainer())
Bark.train(ColoredTestTrainer())

// Control output
Bark.muzzle()    // Disable all logging
Bark.unmuzzle()  // Re-enable logging

// Tag management  
Bark.tag("MySDK")           // Set global tag
Bark.untag()                // Back to auto-detection

// Standard log levels
Bark.v("Verbose message")
Bark.d("Debug message") 
Bark.i("Info message")
Bark.w("Warning message")
Bark.e("Error message", exception)
```

---

## Built-in Trainers

### AndroidLogTrainer
```kotlin
Bark.train(AndroidLogTrainer(volume = Level.DEBUG))
```
- ✅ Uses Android's `Log` class
- ✅ Appears in Logcat
- ✅ Disabled during tests
- ✅ Configurable minimum level

### TestTrainer
```kotlin
Bark.train(TestTrainer(volume = Level.INFO))
```
- ✅ Plain console output
- ✅ Only active during tests
- ✅ CI/CD friendly (no ANSI colors)
- ✅ Configurable timestamps

### ColoredTestTrainer
```kotlin
Bark.train(ColoredTestTrainer(volume = Level.DEBUG))
```
- ✅ Colored console output
- ✅ Only active during tests
- ✅ Different colors per log level
- ✅ Enhanced readability

---

## Advanced Usage

### Custom Trainers

Create your own output destinations:

```kotlin
class SlackTrainer(private val webhookUrl: String) : Trainer {
    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal >= Level.ERROR.ordinal) {
            // Send errors to Slack
            sendToSlack("[$tag] $message")
        }
    }
}

// Use it
Bark.train(SlackTrainer("https://hooks.slack.com/..."))
```

### Volume Control

Different trainers can have different volume thresholds:

```kotlin
Bark.train(AndroidLogTrainer(volume = Level.DEBUG))     // Everything to Logcat
Bark.train(FileTrainer(volume = Level.WARNING))         // Warnings+ to file  
Bark.train(CrashReportingTrainer())                      // Errors only (internal logic)
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
//     [1] ColoredTestTrainer
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
            // Production: only errors, and only to crash reporting
            Bark.train(CrashReportingTrainer())
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

---

## Kotlin Multiplatform

barK is built for Kotlin Multiplatform from the ground up:

```kotlin
// Common code
expect fun isRunningTests(): Boolean
expect fun getCallerTag(): String

// Platform implementations
// ✅ Android (available now)
// 🚧 iOS (coming soon)  
// 🚧 JVM (coming soon)
// 🚧 JS (planned)
```

---

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ivangarzab:bark:1.0.0")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.ivangarzab:bark:1.0.0'
}
```

---

## Requirements

- **Android**: API 21+ (Android 5.0)
- **Kotlin**: 1.9.0+
- **Gradle**: 7.0+

---

## Comparison

### vs. Timber

| Feature | barK | Timber |
|---------|------|--------|
| Auto-tag detection | ✅ | ✅ |
| Test environment detection | ✅ | ❌ |
| Kotlin Multiplatform | ✅ | ❌ |
| Themed API | ✅ | ❌ |
| Multiple trainers | ✅ | ✅ |
| Runtime muzzling | ✅ | ❌ |

### vs. Android Log

| Feature | barK | Android Log |
|---------|------|-------------|
| Manual TAG constants | ❌ | ✅ |
| Test environment handling | ✅ | ❌ |
| Multiple outputs | ✅ | ❌ |
| Runtime control | ✅ | ❌ |
| String formatting | ✅ | ❌ |

---

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

```bash
git clone https://github.com/ivangarzab/barK.git
cd barK
./gradlew test
```

---

## License

```
Copyright 2024 Ivan Garza

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

## Changelog

### v1.0.0 (2024-XX-XX)
- 🎉 Initial release
- ✅ Android support with auto-tag detection
- ✅ Smart test environment detection
- ✅ Trainer system with AndroidLog, Test, and ColoredTest trainers
- ✅ Themed API (train, muzzle, tag, untag)
- ✅ Sample app with realistic usage examples

---

**Made with ❤️ for the Kotlin community**

*barK: Because every log deserves a good home* 🐕🏠