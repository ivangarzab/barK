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
        BarkConfig.shared.autoTagDisabled = false // (1)!

        #if DEBUG
        Bark.train(trainer: NSLogTrainer())              // (2)!
        Bark.train(trainer: ColoredUnitTestTrainer())    // (3)!
        #else
        Bark.train(trainer: NSLogTrainer(minLevel: .warning)) // (4)!
        #endif
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
```

1. Enables auto-tag detection from the call stack. Disabled by default due to the performance cost of C interop and Swift symbol demangling — skip this line if you don't need per-class tags.
2. Routes logs through iOS's NSLog system. Visible in Console.app and Xcode's debug console.
3. Colored console output for XCTest runs. Activates automatically when a test environment is detected.
4. In production, only `WARNING` and above reach NSLog. Keeps system logs clean and meaningful.

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

// Temporary tag for a single scope
Bark.heel("Network") {
    Bark.d("Request sent")    // Tag: [Network]
    Bark.i("Response received") // Tag: [Network]
}
Bark.d("Back to normal")    // Tag: [MyApp] (or auto-detect)
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
