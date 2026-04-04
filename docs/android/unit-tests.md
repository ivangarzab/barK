# Android Unit Tests

**barK** automatically detects Android test environments and switches output accordingly — no configuration required.

---

## How Test Detection Works

At runtime, **barK** inspects the JVM classpath for known testing frameworks:

| Framework | Detected via |
|-----------|-------------|
| JUnit 4 | `org.junit.Test` |
| Robolectric | `org.robolectric.RobolectricTestRunner` |
| Espresso | `androidx.test.espresso.Espresso` |
| AndroidX Test | `androidx.test.core.app.ApplicationProvider` |

When any of these are found, `ColoredUnitTestTrainer` activates and system trainers like `AndroidLogTrainer` are suppressed automatically.

---

## Recommended Test Setup

```kotlin title="ExampleTest.kt"
class ExampleTest {

    @Before
    fun setup() {
        Bark.releaseAllTrainers()
        Bark.train(ColoredUnitTestTrainer())  // Colored output with level prefix
    }

    @After
    fun teardown() {
        Bark.releaseAllTrainers()
    }

    @Test
    fun testSomething() {
        Bark.d("Starting test")
        // ...
        Bark.i("Test completed")
    }
}
```

---

## Choosing a Test Trainer

### `ColoredUnitTestTrainer`

ANSI-colored output with automatic level prefixes. Best for local development where your terminal supports color.

```
[D] MyClass: Debug message
[I] MyClass: Info message
[W] MyClass: Warning message
[E] MyClass: Error message
```

### `UnitTestTrainer`

Plain text output with timestamps. Best for CI environments or when color is not supported.

```
12:34:56 [D] MyClass: Debug message
12:34:56 [I] MyClass: Info message
```

---

## Volume Filtering in Tests

Control how much noise appears in test output:

```kotlin
@Before
fun setup() {
    Bark.releaseAllTrainers()
    // Only show warnings and above during this test class
    Bark.train(ColoredUnitTestTrainer(volume = Level.WARNING))
}
```

---

## Multiple Trainers in Tests

```kotlin
@Before
fun setup() {
    Bark.releaseAllTrainers()
    Bark.train(ColoredUnitTestTrainer())          // Console output
    Bark.train(FileTrainer(                        // Also write to file
        volume = Level.ERROR,
        logFile = File("test-errors.log")
    ))
}
```

---

## Robolectric

**barK** works with Robolectric out of the box. Test detection fires before any log call:

```kotlin
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @Before
    fun setup() {
        Bark.train(ColoredUnitTestTrainer())
    }

    @Test
    fun testActivityCreation() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        Bark.d("Activity under test: $activity")
        assertNotNull(activity)
    }
}
```

!!! note
    When using Robolectric, `AndroidLogTrainer` calls are silently dropped since Logcat is unavailable. Use `ColoredUnitTestTrainer` or `UnitTestTrainer` instead.
