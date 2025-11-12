# barK for iOS

This directory contains Swift helpers for a better barK experience on iOS.

## 🚀 Quick Setup

### Step 1: Add barK to your project

Add the barK framework to your iOS project using Swift Package Manager, CocoaPods, or by manually linking the framework.

### Step 2: Add the Swift Extensions (Recommended)

For a cleaner, more idiomatic Swift API, copy `BarkExtensions.swift` to your iOS project:

1. Drag `BarkExtensions.swift` into your Xcode project
2. Make sure it's added to your app target
3. That's it! You can now use the clean API

### Step 3: Start Logging!

```swift
import shared

// Initialize barK (usually in AppDelegate or App struct)
Bark.train(trainer: ColoredUnitTestTrainer())

// Use the clean API ✨
Bark.i("App started successfully")
Bark.d("Debug information")
Bark.w("This is a warning")
Bark.e("An error occurred", throwable: someError)
```

## 📝 API Comparison

### Without Extensions (Default)
```swift
Bark.shared.i(message: "Hello", throwable: nil)
Bark.shared.e(message: "Error", throwable: someKotlinError)
Bark.shared.train(trainer: ColoredUnitTestTrainer())
```

### With Extensions (Clean!)
```swift
Bark.i("Hello")
Bark.e("Error", throwable: someSwiftError)  // Automatically converted!
Bark.train(trainer: ColoredUnitTestTrainer())
```

## 🎨 Available Trainers

### For Testing
- **`UnitTestTrainer`** - Plain text output, perfect for Xcode test console
- **`ColoredUnitTestTrainer`** - Automatically detects environment:
  - ✅ Shows colors in CI/terminal (GitHub Actions, command line, Fastlane)
  - ❌ Falls back to plain text in Xcode test console (Xcode limitation)

### For Production
- **`NSLogTrainer`** - Routes logs through iOS's NSLog system

## 📚 Example Usage

```swift
import shared

@main
struct MyApp: App {
    init() {
        #if DEBUG
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose))
        #else
        Bark.train(trainer: NSLogTrainer(volume: Level.info))
        #endif

        Bark.i("App initialized")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## 🔧 Configuration

```swift
// Set volume level to filter logs
Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.warning))  // Only warnings and above

// Enable/disable timestamps
Bark.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: true))

// Muzzle all logging
Bark.muzzle()

// Custom global tag
Bark.tag("MyApp")

// Release all trainers
Bark.releaseAllTrainers()
```

## 🎯 Best Practices

1. **Use ColoredUnitTestTrainer for tests** - It automatically adapts to the environment
2. **Configure volume levels** - Use `Level.verbose` for debug, `Level.info` for production
3. **Add the Swift extensions** - Much cleaner API for iOS developers
4. **Use different trainers per configuration** - DEBUG uses test trainers, RELEASE uses NSLog

## 🤔 Why the Extensions?

Kotlin/Native exports Kotlin `object` types as `ClassName.shared` in Swift. The extensions provide a wrapper that:
- ✅ Removes the `.shared` prefix for cleaner syntax
- ✅ Converts Swift `Error` to Kotlin `Throwable` automatically
- ✅ Provides more idiomatic Swift parameter names
- ✅ Adds proper Swift documentation

This is a common pattern in Kotlin Multiplatform libraries (similar to SQLDelight, Koin, etc.)!

## 📖 Full API Reference

### Logging Methods
- `Bark.v(_ message: String, throwable: Error? = nil)` - Verbose
- `Bark.d(_ message: String, throwable: Error? = nil)` - Debug
- `Bark.i(_ message: String, throwable: Error? = nil)` - Info
- `Bark.w(_ message: String, throwable: Error? = nil)` - Warning
- `Bark.e(_ message: String, throwable: Error? = nil)` - Error

### Configuration
- `Bark.train(trainer: Trainer)` - Add a trainer
- `Bark.untrain(trainer: Trainer)` - Remove a trainer
- `Bark.releaseAllTrainers()` - Clear all trainers
- `Bark.muzzle()` - Disable all logging
- `Bark.unmuzzle()` - Re-enable logging
- `Bark.tag(_ tag: String)` - Set global tag
- `Bark.untag()` - Remove global tag
- `Bark.getStatus() -> String` - Get configuration info