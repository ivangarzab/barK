# Advanced Usage

This section covers **barK**'s deeper configuration options — volume control, tagging strategies, runtime control, and the trainer system internals.

---

## Volume Control

Each trainer has an independent volume threshold. Logs below the threshold are silently ignored by that trainer, but may still be handled by others:

```kotlin
// Logcat gets everything
Bark.train(AndroidLogTrainer(volume = Level.VERBOSE))

// File only gets warnings and above
Bark.train(FileTrainer(volume = Level.WARNING, logFile = File("app.log")))

// Crash reporter only gets errors
Bark.train(CrashReportingTrainer(volume = Level.ERROR))

Bark.v("Logcat only")
Bark.w("Logcat + file")
Bark.e("All three trainers")
```

**Level hierarchy:**

| Level | Value |
|-------|-------|
| `VERBOSE` | 0 |
| `DEBUG` | 1 |
| `INFO` | 2 |
| `WARNING` | 3 |
| `ERROR` | 4 |
| `CRITICAL` | 5 |

---

## Tagging

### Auto-Detection (Default)

**barK** reads the calling class name from the stack trace on every log call:

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

Set a fixed tag that applies across all classes — useful for SDK development or feature-level grouping:

```kotlin
Bark.tag("PAYMENT_SDK")

// All calls now use [PAYMENT_SDK]
class PaymentService { fun process() { Bark.d("Processing") } }
class RefundService  { fun refund()  { Bark.d("Refunding")  } }

Bark.untag()  // Return to auto-detection
```

---

## Runtime Control

### Muzzle / Unmuzzle

Silence all output without removing trainers:

```kotlin
Bark.muzzle()
Bark.d("This won't appear anywhere")
Bark.e("Neither will this")

Bark.unmuzzle()
Bark.d("Back to normal")
```

!!! tip "SDK use case"
    Expose `muzzle()` / `unmuzzle()` through your SDK's public API so integrators can silence your logs without removing their own trainers.

### Status Inspection

```kotlin
println(Bark.getStatus())
```

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

### Adding

```kotlin
Bark.train(AndroidLogTrainer())
Bark.train(FileTrainer(logFile = File("app.log")))
```

### Removing

```kotlin
val trainer = AndroidLogTrainer()
Bark.train(trainer)

// Later...
Bark.untrain(trainer)
```

### Clearing All

```kotlin
Bark.releaseAllTrainers()
```

---

## The Pack System

`Pack` categorizes trainers and enforces a one-per-category rule (except `CUSTOM`):

| Pack | Used by | Rule |
|------|---------|------|
| `CONSOLE` | `UnitTestTrainer`, `ColoredUnitTestTrainer` | Only one active at a time |
| `SYSTEM` | `AndroidLogTrainer`, `NSLogTrainer` | Only one active at a time |
| `FILE` | `FileTrainer` | Only one active at a time |
| `CUSTOM` | Your custom trainers | Multiple allowed |

Training a new trainer into an occupied pack **replaces** the existing one:

```kotlin
Bark.train(AndroidLogTrainer())       // SYSTEM slot: AndroidLogTrainer
Bark.train(UnitTestTrainer())         // SYSTEM slot replaced — now UnitTestTrainer

// But CUSTOM trainers stack
Bark.train(SlackTrainer())            // CUSTOM #1
Bark.train(ErrorTracker())            // CUSTOM #2 — both remain active
```

---

## Best Practices

1. **Match volume to environment** — `VERBOSE` in debug, `WARNING`+ in production
2. **Prefer auto-tags** — only use global tags for SDKs or cross-class grouping
3. **Multiple trainers for multiple destinations** — Logcat + file + crash reporting
4. **Expose muzzle controls in SDKs** — let integrators decide what they see
5. **Use `CUSTOM` pack for analytics** — track behavior without displacing other trainers
6. **Always clean up in tests** — call `Bark.releaseAllTrainers()` in `@After`/`tearDown`
