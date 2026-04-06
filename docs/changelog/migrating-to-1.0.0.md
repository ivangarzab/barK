# Migrating from 0.2.x to 1.0.0

This guide covers every change you need to make when upgrading barK to v1.0.0.

---

## Maven coordinates

The library's Maven group ID has changed.

=== "Kotlin DSL"
    ```kotlin title="build.gradle.kts"
    // Before
    implementation("com.ivangarzab:bark:0.2.x")

    // After
    implementation("com.ivangarzab.bark:bark:1.0.0")
    ```

=== "Groovy DSL"
    ```groovy title="build.gradle"
    // Before
    implementation 'com.ivangarzab:bark:0.2.x'

    // After
    implementation 'com.ivangarzab.bark:bark:1.0.0'
    ```

---

## `Trainer.volume` renamed to `Trainer.minLevel`

The `volume` property on the `Trainer` interface has been renamed to `minLevel` to better describe its purpose.

If you have a **custom Trainer**, update the property name:

```kotlin
// Before
class MyTrainer : Trainer {
    override val volume: Level = Level.DEBUG
    ...
}

// After
class MyTrainer : Trainer {
    override val minLevel: Level = Level.DEBUG
    ...
}
```

Similarly, if you instantiate a **built-in trainer** with a named argument:

```kotlin
// Before
UnitTestTrainer(volume = Level.INFO)

// After
UnitTestTrainer(minLevel = Level.INFO)
```

---

## `Pack.CONSOLE` renamed to `Pack.TEST`

The `CONSOLE` value in the `Pack` enum has been renamed to `TEST`. This better reflects its role: It marks trainers that are active during test runs, not generic console output.

```kotlin
// Before
if (trainer.pack == Pack.CONSOLE) { ... }

// After
if (trainer.pack == Pack.TEST) { ... }
```

---

## **iOS:** `ColoredUnitTestTrainer` removed

`ColoredUnitTestTrainer` has been removed from the iOS target. The standard `UnitTestTrainer` now uses `NSLog` for output and is the recommended trainer for test environments on iOS.

```swift
// Before
Bark.shared.train(trainer: ColoredUnitTestTrainer())

// After
Bark.shared.train(trainer: UnitTestTrainer())
```

---

## Behavioral changes

These require no code changes but are worth knowing about.

**`NSLogTrainer` now silences itself during tests.**
Previously it logged regardless of environment. It now backs off when a test run is detected, consistent with how `AndroidLogTrainer` behaves. `UnitTestTrainer` takes over in test contexts.

**`AndroidTestLogTrainer` is now `Pack.TEST`.**
It was previously categorized as `Pack.SYSTEM`. If you relied on Pack-based deduplication rules involving this trainer, review your trainer setup.

**iOS `UnitTestTrainer` switched from `print()` to `NSLog()`.**
Output is now routed through the system logger, making it visible in Console.app and CI log streams.

---

## New in 1.0.0

### `Bark.wtf()`

A new `wtf()` logging function for the `CRITICAL` level — use it for unrecoverable states:

```kotlin
Bark.wtf("Database is corrupted, cannot continue")
Bark.wtf("Fatal error", exception)
```

### `Bark.heel()`

Log a block of code under a temporary tag without changing the global tag:

```kotlin
Bark.heel("NetworkLayer") {
    Bark.d("Sending request to $url")
    Bark.d("Headers: $headers")
}
// Previous tag is restored automatically here
```

Scopes can be nested. The tag reverts after the block completes, even if an exception is thrown.
