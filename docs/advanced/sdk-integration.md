# SDK Integration Guide

**barK** is designed for SDK developers who need flexible, controllable logging that integrators can customize or disable.

---

## Why barK for SDKs?

SDKs have unique logging requirements:

- :material-tune: **Flexibility** — Different environments need different logging strategies
- :material-eye-outline: **Transparency** — Integrators want to see what your SDK is doing
- :material-toggle-switch-outline: **Control** — Integrators want to control verbosity or disable it entirely
- :material-filter-outline: **Non-intrusive** — SDK logs shouldn't pollute integrator's logs

**barK** addresses all of these through its trainer system, runtime control, and global tagging.

---

## SDK Setup

### Initialize barK in Your SDK

Use a global tag so all logs from your SDK are clearly identifiable:

```kotlin title="MySDK.kt"
fun initialize(context: Context) {
    if (BuildConfig.DEBUG) {
        Bark.train(AndroidLogTrainer(volume = Level.DEBUG))
    } else {
        Bark.train(CrashReportingTrainer(volume = Level.ERROR)) // (1)!
    }
    Bark.tag("MySDK") // (2)!
    Bark.i("SDK initialized v${BuildConfig.VERSION_NAME}")
}
```

1. In production, only route errors to your crash reporter (don't spam the integrator's Logcat.)
2. A global tag means every log from your SDK is clearly prefixed with `[MySDK]`, making it easy for integrators to filter.

### 2. Log Throughout

```kotlin
class PaymentProcessor {
    fun processPayment(amount: Double) {
        Bark.d("Processing payment: $$amount")
        try {
            val result = apiClient.charge(amount)
            Bark.i("Payment successful: ${result.transactionId}")
        } catch (e: NetworkException) {
            Bark.e("Payment failed: network error", e)
            throw e
        }
    }
}
```

---

## The Integrator's Side

Integrators who also use **barK** can interact with the same global `Bark` instance your SDK uses. This is both a feature and a responsibility — document your SDK's logging behavior clearly.

### Silence Your SDK's Logs

```kotlin
// In the integrator's app
Bark.muzzle()           // Silences all barK output, including your SDK's
MySdk.doSomething()     // No logs
Bark.unmuzzle()
```

### Add Their Own Trainer

An integrator can stack their own trainer alongside yours:

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Bark.train(SentryTrainer(volume = Level.WARNING)) // (1)!
    }
}
```

1. Using `Pack.CUSTOM`aggregates into all other Trainers without disruption.

### Replace Your System Trainer

An integrator can replace your `SYSTEM` trainer with their own:

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Bark.train(AndroidLogTrainer(volume = Level.INFO)) // (1)!
    }
}
```

1. Trains a new `SYSTEM` trainer :arrow-right: replaces whichever `SYSTEM` trainer your SDK registered.

---

## Best Practices

- **Use a global tag** — makes your SDK's logs easy to identify and filter
- **Default to minimal production logging** — errors only, or muzzled entirely
- **Document Pack usage** — tell integrators which Pack your trainers use, so they know what they can replace
