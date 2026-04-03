# barK

**A lightweight, extensible logging library for Kotlin Multiplatform** with automatic tag detection and smart test environment handling.

[![Maven Central](https://img.shields.io/maven-central/v/com.ivangarzab/bark.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.ivangarzab/bark)
[![Build](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml/badge.svg?branch=main)](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-orange?logo=kotlin)

---

## Why barK?

<div class="grid cards" markdown>

-   :material-tag-outline: **Automatic tag detection**

    ---
    No more manual `TAG` constants. barK reads the calling class name from the stack trace automatically.

-   :material-flask-outline: **Smart test detection**

    ---
    Automatically switches between system output and colored console output depending on whether you're in a test run.

-   :material-puzzle-outline: **Trainer system**

    ---
    Plug in any number of output destinations — Logcat, NSLog, files, crash reporters, or your own.

-   :material-cellphone-link: **Kotlin Multiplatform**

    ---
    Full platform parity across Android and iOS from a single shared API.

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
            Bark.train(ColoredUnitTestTrainer()) // auto-activates in tests
        }
    }
    ```

    **Log anywhere:**

    ```kotlin
    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Bark.d("Activity created")          // Tag: [MainActivity]
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
            Bark.train(trainer: NSLogTrainer(volume: .warning))
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
            Bark.d("Saving user: \(user.name)")  // Tag: [UserRepository]
            Bark.i("User saved successfully")
        }
    }
    ```

    !!! note
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
| `Bark.wtf()` | CRITICAL | Fatal, unrecoverable errors |

---

## Go Further

<div class="grid cards" markdown>

-   :material-book-open-outline: **Advanced Usage**

    ---
    Custom trainers, volume control, runtime muzzling, and platform-specific notes.

    [:octicons-arrow-right-24: Advanced Usage](ADVANCED_USAGE.md)

-   :material-package-variant-closed: **SDK Integration**

    ---
    Using barK inside an SDK — integrator controls, environment-specific setup, best practices.

    [:octicons-arrow-right-24: SDK Integration](SDK_INTEGRATION.md)

</div>
