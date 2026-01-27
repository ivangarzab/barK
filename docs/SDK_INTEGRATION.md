# SDK Integration Guide

barK is designed for SDK developers who need flexible, controllable logging that integrators can customize or disable.

---

## Why barK for SDKs?

SDKs have unique logging requirements:
- **Flexibility** - Different environments (development, staging, production) need different logging strategies
- **Transparency** - Integrators want to see what your SDK is doing
- **Control** - Integrators want to control log verbosity or disable it entirely
- **Non-intrusive** - SDK logs shouldn't pollute integrator's logs

barK solves all of these with its trainer system, runtime control, and global tagging.

---

## Basic SDK Setup

### 1. Initialize barK in Your SDK

```kotlin
class MySDK {
    companion object {
        private var initialized = false
    }

    fun initialize(context: Context, config: SDKConfig) {
        if (initialized) return
        initialized = true

        // Smart trainer setup based on build type
        if (BuildConfig.DEBUG) {
            Bark.train(AndroidLogTrainer(volume = Level.DEBUG))
        } else {
            // Production: only errors to crash reporting
            Bark.train(CrashReportingTrainer(volume = Level.ERROR))
        }

        // Use global tag for all SDK logs
        Bark.tag("MySDK")

        Bark.i("SDK initialized v${BuildConfig.VERSION_NAME}")
    }

    fun performOperation() {
        Bark.d("Starting operation")
        try {
            // Your SDK logic
            Bark.i("Operation completed successfully")
        } catch (e: Exception) {
            Bark.e("Operation failed", e)
            throw e
        }
    }
}
```

### 2. Use barK Throughout Your SDK

```kotlin
class PaymentProcessor {
    fun processPayment(amount: Double) {
        Bark.d("Processing payment: $$amount")  // Tag: [MySDK]

        try {
            val result = apiClient.charge(amount)
            Bark.i("Payment successful: ${result.transactionId}")
        } catch (e: NetworkException) {
            Bark.e("Payment failed: network error", e)
            throw e
        }
    }
}

class UserManager {
    fun login(username: String) {
        Bark.d("User login attempt: $username")  // Tag: [MySDK]

        // Login logic
        Bark.i("User logged in successfully")
    }
}
```

---

## Integrator Control

Give integrators control over your SDK's logging:

### Option 1: Muzzle/Unmuzzle

The simplest approach - let integrators disable all SDK logging:

```kotlin
// In your SDK's public API
class MySDK {
    fun enableLogging(enabled: Boolean) {
        if (enabled) {
            Bark.unmuzzle()
        } else {
            Bark.muzzle()
        }
    }
}
```

**Integrator usage:**
```kotlin
// Disable SDK logs
mySDK.enableLogging(false)

// Re-enable SDK logs
mySDK.enableLogging(true)
```

### Option 2: Retraining

Allow integrators to provide their own trainers:

```kotlin
class MySDK {
    fun setLogTrainer(trainer: Trainer?) {
        Bark.releaseAllTrainers()
        trainer?.let { Bark.train(it) }
    }

    fun setLogLevel(level: Level) {
        Bark.releaseAllTrainers()
        Bark.train(AndroidLogTrainer(volume = level))
    }
}
```

**Integrator usage:**
```kotlin
// Use integrator's preferred trainer
mySDK.setLogTrainer(AndroidLogTrainer(volume = Level.WARNING))

// Or set custom log level
mySDK.setLogLevel(Level.ERROR)  // Only errors and critical

// Or disable entirely
mySDK.setLogTrainer(null)
```

---

## Environment-Specific Logging

### Development

```kotlin
if (BuildConfig.DEBUG) {
    Bark.train(AndroidLogTrainer(volume = Level.VERBOSE))
    Bark.train(ColoredUnitTestTrainer(volume = Level.VERBOSE))
    Bark.tag("MySDK-DEV")
}
```

### Staging

```kotlin
if (BuildConfig.BUILD_TYPE == "staging") {
    Bark.train(AndroidLogTrainer(volume = Level.INFO))
    Bark.train(FileTrainer(volume = Level.WARNING, logFile = File("sdk.log")))
    Bark.tag("MySDK-STAGING")
}
```

### Production

```kotlin
if (BuildConfig.BUILD_TYPE == "release") {
    // Only errors to crash reporting, no console logs
    Bark.train(CrashReportingTrainer(volume = Level.ERROR))
    Bark.tag("MySDK")
}
```

---

## Testing Your SDK

barK's test detection makes SDK testing easy:

```kotlin
class PaymentProcessorTest {
    @Before
    fun setup() {
        // Automatically uses ColoredUnitTestTrainer if trained
        Bark.releaseAllTrainers()
        Bark.train(ColoredUnitTestTrainer())
        Bark.tag("TEST")
    }

    @Test
    fun testPaymentFlow() {
        Bark.d("Starting payment test")

        val processor = PaymentProcessor()
        processor.processPayment(100.0)

        Bark.i("Payment test completed")
    }
}
```

barK automatically detects test environments and switches to console output.

---

## Best Practices for SDK Developers

### 1. Consider Using Global Tags
```kotlin
Bark.tag("YourSDKName")
```

This makes it easy for integrators to filter your SDK's logs.

### 2. Provide Logging Controls
```kotlin
class YourSDK {
    fun setLoggingEnabled(enabled: Boolean) { /* ... */ }
    fun setLogLevel(level: Level) { /* ... */ }
}
```

### 3. Log at Appropriate Levels
- **VERBOSE/DEBUG** - Internal state, data flow
- **INFO** - Important operations, successful completions
- **WARNING** - Recoverable errors, deprecated API usage
- **ERROR** - Failures that affect functionality
- **CRITICAL** - Fatal errors requiring immediate attention

### 4. Avoid Logging Sensitive Data
```kotlin
// Bad
Bark.d("User password: $password")

// Good
Bark.d("User authentication successful")
```

### 5. Use Structured Logging
```kotlin
// Consistent format makes logs easier to parse
Bark.i("operation=payment status=success transaction_id=${txId} amount=${amount}")
```

### 6. Document Logging Behavior
Tell integrators:
- Default log level
- How to disable logging
- How to customize trainers
- What gets logged at each level

---

## Example: Complete SDK Implementation

```kotlin
class PaymentSDK private constructor() {

    companion object {
        @Volatile
        private var instance: PaymentSDK? = null

        fun getInstance(): PaymentSDK {
            return instance ?: synchronized(this) {
                instance ?: PaymentSDK().also { instance = it }
            }
        }
    }

    private var initialized = false

    fun initialize(context: Context, config: PaymentConfig) {
        if (initialized) {
            Bark.w("SDK already initialized")
            return
        }

        setupLogging(config)
        Bark.tag("PaymentSDK")

        Bark.i("Initializing SDK v${BuildConfig.VERSION_NAME}")

        // Initialize SDK components
        initialized = true
        Bark.i("SDK initialized successfully")
    }

    private fun setupLogging(config: PaymentConfig) {
        when {
            !config.loggingEnabled -> {
                Bark.muzzle()
            }
            config.customTrainer != null -> {
                Bark.train(config.customTrainer)
            }
            else -> {
                if (BuildConfig.DEBUG) {
                    Bark.train(AndroidLogTrainer(volume = config.logLevel))
                    Bark.train(ColoredUnitTestTrainer())
                } else {
                    Bark.train(CrashReportingTrainer(volume = Level.ERROR))
                }
            }
        }
    }

    fun setLoggingEnabled(enabled: Boolean) {
        if (enabled) Bark.unmuzzle() else Bark.muzzle()
    }

    fun processPayment(request: PaymentRequest): PaymentResult {
        Bark.d("Processing payment: amount=${request.amount} currency=${request.currency}")

        try {
            val result = performPayment(request)
            Bark.i("Payment successful: transaction_id=${result.transactionId}")
            return result
        } catch (e: NetworkException) {
            Bark.e("Payment failed: network error", e)
            throw e
        } catch (e: Exception) {
            Bark.e("Payment failed: unexpected error", e)
            throw PaymentException("Payment processing failed", e)
        }
    }

    private fun performPayment(request: PaymentRequest): PaymentResult {
        // Payment logic
        return PaymentResult(/* ... */)
    }
}

data class PaymentConfig(
    val loggingEnabled: Boolean = true,
    val logLevel: Level = Level.INFO,
    val customTrainer: Trainer? = null
)
```

**Integrator usage:**

```kotlin
// Default setup
PaymentSDK.getInstance().initialize(
    context,
    PaymentConfig()
)

// Custom setup
PaymentSDK.getInstance().initialize(
    context,
    PaymentConfig(
        loggingEnabled = true,
        logLevel = Level.WARNING,
        customTrainer = MyCustomTrainer()
    )
)

// Disable logging later
PaymentSDK.getInstance().setLoggingEnabled(false)
```

---

## See Also

- [Advanced Usage Guide](ADVANCED_USAGE.md) - Custom trainers, volume control, runtime management
- [Main README](../README.md) - Quick start and features
- [iOS Integration Guide](../ios/README.md) - iOS-specific SDK integration