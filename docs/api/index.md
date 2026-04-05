# API Reference

**barK**'s public API is intentionally small. Everything revolves around a single object — `Bark` — and a handful of supporting types.

[:octicons-arrow-right-24: Browse the full API Reference](reference/index.html){ .md-button .md-button--primary }

---

## Core Types

<div class="grid cards" markdown>

-   **`Bark`**

    ---
    The singleton entry point. All logging calls, trainer management, and runtime control go through here.

-   **`Trainer`**

    ---
    The interface for log output destinations. Implement this to send logs anywhere — Logcat, NSLog, files, crash reporters.

-   **`Level`**

    ---
    Log severity: `VERBOSE` → `DEBUG` → `INFO` → `WARNING` → `ERROR` → `CRITICAL`. Each trainer filters independently.

-   **`Pack`**

    ---
    Trainer category: `CONSOLE`, `SYSTEM`, `FILE`, `CUSTOM`. Prevents accidentally registering duplicate output destinations.

</div>

---

## Quick Reference

### Logging

| Method | Level | Use for |
|--------|-------|---------|
| `Bark.v(message, throwable?)` | VERBOSE | Detailed diagnostic info |
| `Bark.d(message, throwable?)` | DEBUG | Development debugging |
| `Bark.i(message, throwable?)` | INFO | Key operations, state changes |
| `Bark.w(message, throwable?)` | WARNING | Recoverable issues |
| `Bark.e(message, throwable?)` | ERROR | Failures affecting functionality |
| `Bark.wtf(message, throwable?)` | CRITICAL | Unrecoverable states |

### Trainers

| Method | Description |
|--------|-------------|
| `Bark.train(trainer)` | Register a trainer |
| `Bark.untrain(trainer)` | Remove a specific trainer |
| `Bark.releaseAllTrainers()` | Remove all trainers |

### Control

| Method | Description |
|--------|-------------|
| `Bark.muzzle()` | Silence all output |
| `Bark.unmuzzle()` | Resume output |
| `Bark.tag(tag)` | Set a global tag override |
| `Bark.untag()` | Remove the global tag |
| `Bark.heel(tag, block())` | Log within a temporary tag scope |
| `Bark.getStatus()` | Returns a status string with current configuration |

---

!!! tip "Looking for built-in trainers?"
    See the [Android Guide](../android/index.md) or [iOS Guide](../ios/index.md) for the full list of platform trainers, or [Custom Trainers](../advanced/custom-trainers.md) to build your own.