# Custom Trainers

The `Trainer` interface is **barK**'s extension point. Implement it to send logs anywhere — crash reporters, Slack, analytics pipelines, files, or any custom destination.

---

## The Trainer Interface

```kotlin title="Trainer.kt"
interface Trainer {
    val pack: Pack      // Categorizes the trainer; controls replacement behavior
    val volume: Level   // Minimum level this trainer will handle

    fun handle(level: Level, tag: String, message: String, throwable: Throwable?)
}
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

### Choosing a Pack

Training a new trainer into an occupied pack **replaces** the existing one:

```kotlin
Bark.train(AndroidLogTrainer())       // SYSTEM slot: AndroidLogTrainer
Bark.train(UnitTestTrainer())         // SYSTEM slot replaced — now UnitTestTrainer

// But CUSTOM trainers stack
Bark.train(SlackTrainer())            // CUSTOM #1
Bark.train(ErrorTracker())            // CUSTOM #2 — both remain active
```

Use `Pack.CUSTOM` for most custom trainers — it allows multiple instances to coexist:

```kotlin
Bark.train(SlackTrainer())      // CUSTOM #1
Bark.train(ErrorTracker())      // CUSTOM #2
Bark.train(AnalyticsLogger())   // CUSTOM #3 — all three remain active
```

The `Pack.FILE` is reserved for a single file logger, as to avoid having multiple file writers:

```kotlin
Bark.train(FileTrainer(logFile = File("v1.log")))
Bark.train(FileTrainer(logFile = File("v2.log")))  // Replaces the first one
```

---

## Volume Filtering

**barK** calls `handle()` for every log — your trainer is responsible for its own threshold check, or you can rely on **barK**'s built-in filtering by setting `volume` correctly:

```kotlin
class MyTrainer(
    override val volume: Level = Level.INFO,
) : Trainer {
    override val pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        // handle volume filter for custom functionality
        if (level.ordinal < volume.ordinal) return
        // ... send log
    }
}
```

---

## Examples

### Error Tracking (e.g. Sentry, Crashlytics)

```kotlin title="CrashReportingTrainer.kt"
class CrashReportingTrainer(
    override val volume: Level = Level.ERROR,
) : Trainer {
    override val pack: Pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        ErrorTracker.log(level, tag, message, throwable)
    }
}
```

### Slack Notifications

```kotlin title="SlackTrainer.kt"
class SlackTrainer(
    override val volume: Level = Level.WARNING,
    private val webhookUrl: String,
) : Trainer {
    override val pack: Pack = Pack.CUSTOM

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        SlackClient.send(webhookUrl, "[${level.name}] $tag: $message")
    }
}
```

### File Logging

```kotlin title="FileTrainer.kt"
class FileTrainer(
    override val volume: Level = Level.WARNING,
    private val logFile: File,
) : Trainer {
    override val pack: Pack = Pack.FILE

    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        logFile.appendText("${level.name} [$tag]: $message\n")
        throwable?.let { logFile.appendText(it.stackTraceToString() + "\n") }
    }
}
```

---

## Registering Custom Trainers

```kotlin
Bark.train(CrashReportingTrainer())
Bark.train(SlackTrainer(webhookUrl = "https://hooks.slack.com/..."))
Bark.train(FileTrainer(logFile = File("app.log")))
```

---

## Testing Your Trainer

```kotlin
class MyTrainerTest {

    @Test
    fun `trainer handles messages above volume threshold`() {
        val received = mutableListOf<String>()
        val trainer = object : Trainer {
            override val pack = Pack.CUSTOM
            override val volume = Level.WARNING
            override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
                received.add(message)
            }
        }

        Bark.releaseAllTrainers()
        Bark.train(trainer)

        Bark.d("Should be ignored")
        Bark.w("Should appear")
        Bark.e("Should appear too")

        assertEquals(2, received.size)
    }
}
```
