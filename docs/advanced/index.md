# Advanced Usage

This section covers **barK**'s deeper configuration options: log Level control, tagging strategies, runtime control, and the trainer system internals.

---

## Tagging

### Auto-Detection

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

!!! note 
    While this functionality is defaulted in Android, iOS auto-detection needs to enabled manually due to its processing cost.

### Global Tag Override

Set a fixed tag that applies across all classes:

```kotlin
Bark.tag("PAYMENTS")

// All calls now use [PAYMENTS]
class PaymentService { fun process() { Bark.d("Processing") } }
class RefundService  { fun refund()  { Bark.d("Refunding")  } }

Bark.untag()  // Return to auto-detection
```

---

## MinLevel Control

Each trainer has an independent minLevel threshold. Logs below the threshold are silently ignored by that trainer, but may still be handled by others:

```kotlin
Bark.train(AndroidLogTrainer(minLevel = Level.VERBOSE))           // (1)!
Bark.train(FileTrainer(minLevel = Level.WARNING, logFile = File("app.log"))) // (2)!
Bark.train(CrashReportingTrainer(minLevel = Level.ERROR))         // (3)!

Bark.v("Logcat only")       // (4)!
Bark.w("Logcat + file")
Bark.e("All three trainers")
```

1. Logcat receives everything — `VERBOSE` and above.
2. The file trainer only writes `WARNING` and above, keeping the log file focused on actionable issues.
3. The crash reporter only activates on `ERROR` and above — high-signal, low-noise.
4. A `VERBOSE` log reaches only Logcat. As severity increases, more trainers pick it up.

**Level hierarchy:**

| Level | Function |
|-------|----------|
| `VERBOSE` | `Bark.v()` |
| `DEBUG` | `Bark.d()` |
| `INFO` | `Bark.i()` |
| `WARNING` | `Bark.w()` |
| `ERROR` | `Bark.e()` |
| `CRITICAL` | `Bark.wtf()` |

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
