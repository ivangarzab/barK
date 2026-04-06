# **barK**

**A lightweight, extensible logging library for Kotlin Multiplatform**.

*Because every log deserves a good home* 🐕🏠

---

## Why barK?

<div class="grid cards" markdown>

-   :material-cellphone-link: **Kotlin Multiplatform**

    ---
    Full platform parity across Android and iOS from a single shared API.

-   :material-tag-outline: **Automatic tag detection**

    ---
    No more manual `TAG` constants. **barK** reads the calling class name from the stack trace automatically.

-   :material-puzzle-outline: **Trainer system**

    ---
    Plug in any number of output destinations: Logcat, NSLog, files, crash reporters, or your own.

-   :material-flask-outline: **Smart test detection**

    ---
    Automatically switches between system output and colored console output depending on whether you're in a test run.

</div>

---

## Installation

Add barK to your project via Maven Central:

=== "Kotlin DSL"

    ```kotlin title="build.gradle.kts"
    repositories {
        mavenCentral()
    }

    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("com.ivangarzab.bark:bark:<version>")
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
                    implementation 'com.ivangarzab.bark:bark:<version>'
                }
            }
        }
    }
    ```

!!! tip "Latest version"
    Check the badge at the top of this page or [Maven Central](https://central.sonatype.com/artifact/com.ivangarzab/bark) for the latest release.

---

## Quick Start

=== "Android"

    **Initialize once** (e.g. in your `Application` class):

    ```kotlin title="MyApplication.kt"
    class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            if (BuildConfig.DEBUG) {
                Bark.train(AndroidLogTrainer())
            }
        }
    }
    ```

    **Log anywhere:**

    ```kotlin
    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Bark.d("Activity created")
            Bark.i("User logged in: ${user.name}")
            Bark.e("Network error", exception)
        }
    }
    ```

=== "iOS"

    **Initialize once** (e.g. in your `App` entry point):

    ```swift title="MyApp.swift"
    @main
    struct MyApp: App {
        init() {
            #if DEBUG
            Bark.train(trainer: NSLogTrainer())
            Bark.train(trainer: ColoredUnitTestTrainer())
            #else
            Bark.train(trainer: NSLogTrainer(minLevel: .warning))
            #endif
        }

        var body: some Scene {
            WindowGroup { ContentView() }
        }
    }
    ```

    **Log anywhere:**

    ```swift
    class UserRepository {
        func saveUser(user: User) {
            Bark.d("Saving user: \(user.name)")
            Bark.i("User saved successfully")
        }
    }
    ```

    !!! tip
        Copy `ios/BarkExtensions.swift` into your app target for the clean `Bark.d(...)` syntax shown above.

---

## Log Levels

| Method | Level | Use for |
|--------|-------|---------|
| `Bark.v()` | VERBOSE | Detailed diagnostic info |
| `Bark.d()` | DEBUG | Development debugging |
| `Bark.i()` | INFO | Key operations, state changes |
| `Bark.w()` | WARNING | Recoverable issues |
| `Bark.e()` | ERROR | Failures affecting functionality |

---

## Choose Your Platform

<div class="grid cards" markdown>

-   :fontawesome-brands-android:{ .lg .middle } **Android Guide**

    ---
    Get started using barK for Android.

    [:octicons-arrow-right-24: Android Guide](android/index.md)

-   :fontawesome-brands-apple:{ .lg } **iOS Guide**

    ---
    Get started using barK for iOS.

    [:octicons-arrow-right-24: iOS Guide](ios/index.md)

</div>
