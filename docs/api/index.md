# API Reference

Full API documentation is generated from source via [Dokka](https://github.com/Kotlin/dokka) and published here on each release.

[:octicons-arrow-right-24: Browse the full API Reference](reference/index.md){ .md-button .md-button--primary }

!!! info
    The API reference is generated from source via Dokka and published alongside this site. If the link above doesn't work yet, refer to the source on [GitHub](https://github.com/ivangarzab/barK/tree/main/shared/src/commonMain).

---

## Quick Reference

### Logging

| Method | Level |
|--------|-------|
| `Bark.v(message, throwable?)` | VERBOSE |
| `Bark.d(message, throwable?)` | DEBUG |
| `Bark.i(message, throwable?)` | INFO |
| `Bark.w(message, throwable?)` | WARNING |
| `Bark.e(message, throwable?)` | ERROR |
| `Bark.wtf(message, throwable?)` | CRITICAL |

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
| `Bark.tag(tag)` | Set a global tag |
| `Bark.untag()` | Remove the global tag |
| `Bark.heel(tag, block)` | Log within a temporary tag scope |
| `Bark.getStatus()` | Returns a status string with current configuration |
