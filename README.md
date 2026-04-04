[![Maven Central](https://img.shields.io/maven-central/v/com.ivangarzab/bark.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.ivangarzab/bark)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-orange?logo=kotlin)
![Android](https://img.shields.io/badge/Android-✔️-green?logo=android)
![iOS](https://img.shields.io/badge/iOS-✔️-lightgrey?logo=apple)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/Heapy/awesome-kotlin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Check](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml/badge.svg?branch=main)](https://github.com/ivangarzab/barK/actions/workflows/unit-tests.yml)

<div align="center">

![wordmark](/assets/wordmark.png)

</div>

***barK**: A simple, light-weight and extensible logging library for Kotlin Multiplatform*

---

## Why barK?

- 🔄 **Kotlin Multiplatform** — Full Android + iOS support
- 🏷️ **Automatic tag detection** — No more manual `TAG` constants
- 🎯 **Trainer system** — Flexible, extensible output destinations
- 🧪 **Smart test detection** — Different output for tests vs. production

## Installation

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.ivangarzab:bark:<version>")
        }
    }
}
```

## Quick Start

```kotlin
// Train once at app startup
Bark.train(AndroidLogTrainer())

// Log anywhere — tag is detected automatically
Bark.d("App started")               // [MainActivity]
Bark.e("Something failed", error)   // [PaymentService]
```

## Sample Apps

**Android:**
```bash
./gradlew :sample-android:installDebug
```

**iOS:** 

Open `sample-ios/barK-sample/barK-sample.xcodeproj` in Xcode and run.

## Documentation

Full documentation, guides, and API reference at **[ivangarzab.github.io/bark](https://ivangarzab.github.io/bark/)**.

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## License

```
Copyright 2025 Ivan Garza

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

<div align="center">

*Because every log deserves a good home* 🐕🏠

**Made with 🖤 for the Kotlin community**

</div>
