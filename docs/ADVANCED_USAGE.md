# Advanced Usage

This guide covers advanced features and customization options for barK.

---

## Custom Trainers

Create your own output destinations by implementing the `Trainer` interface:

```kotlin
// Custom error capturing trainer for warnings, errors, and critical logs
class CustomErrorTrainer(
    override val volume: Level = Level.WARNING,
) : Trainer {
    override val pack: Pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal >= volume.ordinal) {
            // Send to your error tracking service (e.g., Sentry, Firebase Crashlytics)
            ErrorTracker.log(level, tag, message, throwable)
        }
    }
}

// Custom trainer to send Slack notifications
class SlackTrainer(
    override val volume: Level = Level.INFO,
    private val webhookUrl: String
) : Trainer {
    override val pack: Pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal >= volume.ordinal) {
            // Send to Slack webhook
            SlackClient.send(webhookUrl, "[${level.label}] $tag: $message")
        }
    }
}

// File logging trainer
class FileTrainer(
    override val volume: Level = Level.WARNING,
    private val logFile: File
) : Trainer {
    override val pack: Pack = Pack.FILE

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        if (level.ordinal >= volume.ordinal) {
            logFile.appendText("${level.label} $tag: $message\n")
            throwable?.let { logFile.appendText("${it.stackTraceToString()}\n") }
        }
    }
}
```

**Register your custom trainers:**

```kotlin
Bark.train(CustomErrorTrainer())
Bark.train(SlackTrainer(webhookUrl = "https://hooks.slack.com/..."))
Bark.train(FileTrainer(logFile = File("app.log")))
```

---

## Volume Control

Different trainers can have different volume thresholds, allowing you to control what gets logged where:

```kotlin
// Everything to Logcat (verbose and above)
Bark.train(AndroidLogTrainer(volume = Level.VERBOSE))

// Only warnings and above to file
Bark.train(FileTrainer(volume = Level.WARNING))

// Only errors to crash reporting (internal logic in trainer)
Bark.train(CrashReportingTrainer(volume = Level.ERROR))

// Now when you log:
Bark.v("This goes to Logcat only")
Bark.d("This goes to Logcat only")
Bark.i("This goes to Logcat only")
Bark.w("This goes to Logcat AND file")
Bark.e("This goes to ALL trainers")
```

**Level Hierarchy:**
- `VERBOSE` (0)
- `DEBUG` (1)
- `INFO` (2)
- `WARNING` (3)
- `ERROR` (4)
- `CRITICAL` (5)

Trainers only handle logs at or above their volume threshold.

---

## Global vs Auto-Detection Tags

barK offers two tagging modes: automatic detection (default) and global override.

### Auto-Detection (Default)

barK automatically detects the calling class name:

```kotlin
class PaymentService {
    fun processPayment() {
        Bark.d("Processing payment")  // Tag: [PaymentService]
    }
}

class UserRepository {
    fun saveUser(user: User) {
        Bark.i("Saving user: ${user.name}")  // Tag: [UserRepository]
    }
}
```

### Global Tag Override

For SDK integration or consistent tagging across multiple classes:

```kotlin
// Set a global tag
Bark.tag("PAYMENT_SDK")

class PaymentService {
    fun processPayment() {
        Bark.d("Processing payment")  // Tag: [PAYMENT_SDK]
    }
}

class RefundService {
    fun processRefund() {
        Bark.d("Processing refund")  // Tag: [PAYMENT_SDK]
    }
}

// Return to auto-detection
Bark.untag()

Bark.d("Back to auto-detection")  // Tag: [MainActivity] (or wherever called from)
```

**Use cases for global tags:**
- SDK libraries with consistent branding
- Grouping related logs across multiple classes
- Filtering logs by feature/module in production

---

## Runtime Control

Control barK's behavior at runtime without recompiling.

### Muzzle/Unmuzzle

Disable and re-enable all logging:

```kotlin
// Disable all logging
Bark.muzzle()
Bark.d("This won't appear anywhere")
Bark.e("Neither will this")

// Re-enable logging
Bark.unmuzzle()
Bark.d("This will appear")
```

**Use cases:**
- SDK integrators disabling library logs
- Temporarily disabling logs in performance-critical sections
- User preferences for log output

### Status Inspection

Check barK's current configuration:

```kotlin
println(Bark.getStatus())
```

**Output:**
```
Bark Status:
  Muzzled: false
  Tag: auto-detect
  Trainers: 3
    [0] AndroidLogTrainer (volume: DEBUG)
    [1] ColoredUnitTestTrainer (volume: VERBOSE)
    [2] FileTrainer (volume: WARNING)
```

---

## Trainer Management

### Adding Trainers

```kotlin
Bark.train(AndroidLogTrainer())
Bark.train(FileTrainer(logFile = File("app.log")))
```

### Removing Trainers

```kotlin
val trainer = AndroidLogTrainer()
Bark.train(trainer)

// Later, remove it
Bark.untrain(trainer)
```

### Clearing All Trainers

```kotlin
Bark.releaseAllTrainers()
```

**Note:** You cannot have multiple trainers with the same `Pack` (except `Pack.CUSTOM`). Training a new trainer with an existing pack will replace the old one.

---

## Pack System

The `Pack` enum categorizes trainers and prevents duplicates:

- **`Pack.CONSOLE`** - Console output trainers (UnitTestTrainer, ColoredUnitTestTrainer)
- **`Pack.SYSTEM`** - System logging trainers (AndroidLogTrainer, NSLogTrainer)
- **`Pack.FILE`** - File logging trainers
- **`Pack.CUSTOM`** - Custom trainers (allows multiple)

**Example:**

```kotlin
// This replaces any existing SYSTEM trainer
Bark.train(UnitTestTrainer())

// This replaces the AndroidLogTrainer
Bark.train(ColoredUnitTestTrainer())

// But CUSTOM trainers can coexist
Bark.train(SlackTrainer())     // Custom 1
Bark.train(ErrorTracker())     // Custom 2
Bark.train(AnalyticsLogger())  // Custom 3 - all three remain active
```

---

## Platform-Specific Notes

### Android

**Auto-tag detection:**
- Uses `Thread.currentThread().stackTrace`
- Enforces 23-character tag limit (Android Logcat limitation)
- Filters out framework classes automatically

**Test detection:**
- Automatically detects JUnit, Robolectric, Espresso, AndroidX Test
- Switches to console output during tests

### iOS

**Auto-tag detection:**
- Uses `backtrace()` with C interop for stack traces
- Parses Swift mangled names and Kotlin symbols
- **Disabled by default** (performance cost)
- Enable with: `BarkConfig.autoTagDisabled = false`

**Color support:**
- Auto-detects ANSI support via `isatty()` and `TERM` environment variable
- Works in Terminal and CI/CD (GitHub Actions, Fastlane)
- Falls back to plain text in Xcode test console

**Swift API:**
- Copy `BarkExtensions.swift` from `/ios` directory for cleaner API
- Transforms `Bark.shared.d(message:)` → `Bark.d("message")`

---

## Best Practices

1. **Use volume control** - Log verbosely in development, minimally in production
2. **Prefer auto-tags** - Only use global tags when necessary (SDKs, feature grouping)
3. **Multiple trainers** - Different destinations for different purposes (Logcat + File + Crash reporting)
4. **Muzzle in production** - Let integrators control your SDK's logging
5. **Custom trainers for analytics** - Track user behavior, errors, performance metrics
6. **Test your trainers** - barK's test trainers make it easy to verify logging behavior

---

## Examples

### Production Setup

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // Debug: everything to Logcat
            Bark.train(AndroidLogTrainer(volume = Level.VERBOSE))
        } else {
            // Production: warnings+ to crash reporting, errors to analytics
            Bark.train(AndroidLogTrainer(volume = Level.WARNING))
        }
    }
}
```

### Multi-environment Setup (example)

```kotlin
when (BuildConfig.BUILD_TYPE) {
    "debug" -> {
        Bark.train(AndroidLogTrainer())
        Bark.train(ColoredUnitTestTrainer())
    }
    "staging" -> {
        Bark.train(AndroidLogTrainer(volume = Level.INFO))
        Bark.train(FileTrainer(volume = Level.WARNING))
    }
    "release" -> {
        Bark.train(CrashReportingTrainer(volume = Level.ERROR))
    }
}
```

---

## See Also

- [SDK Integration Guide](SDK_INTEGRATION.md) - Using barK in SDKs
- [Main README](../README.md) - Quick start and features
- [iOS Integration Guide](../ios/README.md) - iOS-specific setup