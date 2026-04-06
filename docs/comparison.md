# Comparison

How **barK** stacks up against the alternatives.

---

## barK vs. Android Log

The built-in `android.util.Log` is fine for simple apps but falls apart quickly in larger codebases, test suites, and multiplatform projects.

| Feature                           | barK | Android Log |
|-----------------------------------|:----:|:-----------:|
| Automatic tag detection           | ✅ | ❌ |
| Manual TAG constants required     | ❌ | ✅ |
| Test environment handling         | ✅ | ❌ |
| Multiple output destinations      | ✅ | ❌ |
| Runtime muzzle/unmuzzle           | ✅ | ❌ |
| Log level control per destination | ✅ | ❌ |
| Kotlin Multiplatform (iOS)        | ✅ | ❌ |
| Zero dependencies                 | ✅ | ✅ |

---

## barK vs. Timber

[Timber](https://github.com/JakeWharton/timber) is the most popular Android logging library and a close comparison. **barK** was partly inspired by it.

| Feature                           | barK | Timber |
|-----------------------------------|:----:|:------:|
| Automatic tag detection           | ✅ | ✅ |
| Test environment detection        | ✅ | ❌ |
| Kotlin Multiplatform (iOS)        | ✅ | ❌ |
| Multiple output destinations      | ✅ | ✅ |
| Runtime muzzle/unmuzzle           | ✅ | ❌ |
| Log level control per destination | ✅ | ⚠️ partial |
| Written in Kotlin                 | ✅ | ❌ (Java) |
| KMP-native API                    | ✅ | ❌ |

!!! note
    Timber is Android-only and Java-based. If your project is Android-only and you're already using Timber, it remains a solid choice. **barK** is the better fit for Kotlin Multiplatform projects or teams that want built-in test environment awareness.

---

## barK vs. Napier

[Napier](https://github.com/AAkira/Napier) is a Kotlin Multiplatform logging library, making it the closest direct comparison.

| Feature                           | barK | Napier |
|-----------------------------------|:----:|:------:|
| Automatic tag detection           | ✅ | ✅ |
| Test environment detection        | ✅ | ❌ |
| Runtime muzzle/unmuzzle           | ✅ | ❌ |
| Log level control per destination | ✅ | ✅ |
| Multiple output destinations      | ✅ | ✅ |
| Colored test console output       | ✅ | ❌ |
| iOS auto-tag (Swift symbols)      | ✅ | ❌ |
| Kotlin Multiplatform              | ✅ | ✅ |

---

## Summary

- Coming from **Android Log** → **barK** removes boilerplate and adds multiplatform support.
- Coming from **Timber** → **barK** is the natural upgrade path for KMP projects.
- Comparing with **Napier** → **barK** adds test detection, runtime control, and colored output on the Kotlin side.
