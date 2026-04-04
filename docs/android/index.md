# Android Guide

**barK** integrates seamlessly into any Android project — Kotlin Multiplatform or Android-only — with zero boilerplate and automatic tag detection.

---

## Installation

Add **barK** to your project via Maven Central:

=== "Kotlin DSL"

    ```kotlin title="build.gradle.kts"
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

=== "Groovy"

    ```groovy title="build.gradle"
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

## Initialization

Train **barK** once at app startup — typically in your `Application` class:

```kotlin title="MyApplication.kt"
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Bark.train(AndroidLogTrainer())           // (1)!
            Bark.train(ColoredUnitTestTrainer())      // (2)!
        } else {
            Bark.train(AndroidLogTrainer(volume = Level.WARNING))  // (3)!
        }
    }
}
```

1. Routes all logs to Android Logcat. Uses the `SYSTEM` pack — only one system trainer can be active at a time.
2. Colored console output for unit tests. Activates automatically when a test environment is detected — no extra config needed.
3. In production, only `WARNING` and above reach Logcat. Keeps logs meaningful and reduces noise.

---

## Logging

Use **barK** from anywhere in your app. Tags are detected automatically from the calling class:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Bark.v("Verbose detail")             // Tag: [MainActivity]
        Bark.d("Debug info")
        Bark.i("User logged in: ${user.name}")
        Bark.w("Token expiring soon")
        Bark.e("Network request failed", exception)
        Bark.wtf("Unrecoverable state reached")
    }
}
```

---

## Available Trainers

| Trainer | Pack | Best for |
|---------|------|----------|
| `AndroidLogTrainer` | SYSTEM | Logcat output in app and instrumented tests |
| `AndroidTestLogTrainer` | SYSTEM | Logcat output scoped to test runs |
| `UnitTestTrainer` | CONSOLE | Plain console output in unit tests |
| `ColoredUnitTestTrainer` | CONSOLE | ANSI-colored console output in unit tests |

!!! tip
    `ColoredUnitTestTrainer` automatically detects whether it's running in a test environment and activates accordingly — no configuration needed.

---

## Auto-Tag Detection

**barK** reads the calling class name from the stack trace at runtime. No manual `TAG` constant required:

```kotlin
class UserRepository {
    fun saveUser(user: User) {
        Bark.i("Saving user: ${user.name}")  // Tag: [UserRepository]
    }
}

class PaymentService {
    fun processPayment() {
        Bark.d("Processing payment")         // Tag: [PaymentService]
    }
}
```

!!! note
    Android enforces a 23-character tag limit. **barK** truncates automatically when needed.

---

## Global Tags

Override auto-detection with a consistent tag across all classes — useful for SDK development:

```kotlin
Bark.tag("MySDK")

// All calls now use [MySDK] regardless of calling class
Bark.d("Initializing")
Bark.i("Ready")

Bark.untag()  // Return to auto-detection
```

---

## Runtime Control

```kotlin
Bark.muzzle()    // Silence all output
Bark.unmuzzle()  // Resume output

println(Bark.getStatus())  // Inspect current configuration
```

---

## Sample App

See **barK** in action with the included Android sample:

```bash
./gradlew :sample-android:installDebug
```

The sample demonstrates realistic usage with the repository pattern, multiple trainers, and auto-tag detection across multiple classes.
