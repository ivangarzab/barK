# iOS Guide

**barK** runs on iOS through Kotlin Multiplatform, with full platform parity to the Android API and an optional Swift extension layer for a more idiomatic experience.

---

## Installation

### Step 1 — Add barK to your KMP shared module

```kotlin title="build.gradle.kts"
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.ivangarzab:bark:<version>")
        }
    }
}
```

The iOS framework is generated automatically when you build the shared module.

### Step 2 — Import in Swift

```swift
import shared  // Your KMP shared module name
```

### Step 3 — Add BarkExtensions.swift (Recommended)

Copy `ios/BarkExtensions.swift` from the repo into your Xcode project and add it to your app target. This provides a cleaner, more idiomatic Swift API:

=== "Without extensions"

    ```swift
    Bark.shared.d(message: "Hello", throwable: nil)
    Bark.shared.e(message: "Error", throwable: someKotlinError)
    Bark.shared.train(trainer: NSLogTrainer())
    ```

=== "With extensions"

    ```swift
    Bark.d("Hello")
    Bark.e("Error", throwable: someSwiftError)  // Swift Error auto-converted
    Bark.train(trainer: NSLogTrainer())
    ```

!!! tip "Why the extensions?"
    Kotlin/Native exports `object` types as `ClassName.shared` in Swift. The extensions remove the `.shared` prefix, convert Swift `Error` to Kotlin `Throwable` automatically, and provide idiomatic Swift parameter names.

---

## Initialization

Set up **barK** once at app launch — typically in your `App` struct:

```swift title="MyApp.swift"
@main
struct MyApp: App {
    init() {
        // Optional: enable auto-tag detection (disabled by default — has performance cost)
        BarkConfig.shared.autoTagDisabled = false

        #if DEBUG
        Bark.train(trainer: NSLogTrainer())
        Bark.train(trainer: ColoredUnitTestTrainer())  // Auto-activates in XCTest runs
        #else
        Bark.train(trainer: NSLogTrainer(volume: .warning))
        #endif
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
```

---

## Logging

```swift
class UserRepository {
    func saveUser(user: User) {
        Bark.v("Verbose detail")
        Bark.d("Saving user: \(user.name)")  // Tag: [UserRepository] if auto-tag enabled
        Bark.i("User saved successfully")
        Bark.w("Cache invalidated")
        Bark.e("Save failed", throwable: error)
    }
}
```

---

## Available Trainers

| Trainer | Pack | Best for |
|---------|------|----------|
| `NSLogTrainer` | SYSTEM | System logging via NSLog |
| `UnitTestTrainer` | CONSOLE | Plain console output in tests |
| `ColoredUnitTestTrainer` | CONSOLE | ANSI-colored output in Terminal/CI |

---

## Auto-Tag Detection

Unlike Android, auto-tag detection on iOS is **disabled by default** due to the performance cost of C interop and Swift symbol demangling:

```swift
// Enable auto-tag detection
BarkConfig.shared.autoTagDisabled = false

// Now barK reads the calling class name from the stack trace
Bark.d("Message")  // Tag: [MyClass]
```

!!! warning "Performance"
    Auto-tag uses `backtrace()` with C interop on every log call. Leave it disabled in performance-critical code or production if tagging isn't needed.

---

## Global Tags

A simpler alternative to auto-detection — set a fixed tag for all output:

```swift
Bark.tag("MyApp")
Bark.d("Initializing")  // Tag: [MyApp]

Bark.untag()            // Return to auto-detection (or no tag if disabled)
```

---

## Runtime Control

```swift
Bark.muzzle()           // Silence all output
Bark.unmuzzle()         // Resume output
Bark.releaseAllTrainers() // Remove all trainers

print(Bark.getStatus()) // Inspect current configuration
```

---

## Sample App

See **barK** in action with the included iOS sample app:

```
sample-ios/barK-sample/barK-sample.xcodeproj
```

Open in Xcode and run. The sample mirrors the Android sample with the same repository pattern and demonstrates realistic usage across multiple Swift classes.
