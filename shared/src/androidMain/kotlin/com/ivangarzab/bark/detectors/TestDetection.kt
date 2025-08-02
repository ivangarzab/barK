package com.ivangarzab.bark.detectors

/**
 * Detects if the code is currently running in a test environment on Android.
 *
 * This implementation uses runtime detection by examining the call stack
 * for test-related classes and frameworks. This approach is reliable across
 * different build configurations and doesn't require build-time setup.
 *
 * @return true if running tests, false otherwise
 */
internal actual fun isRunningTests(): Boolean {
    return isTestRunnerPresent()
}

/**
 * Detects test environment by checking for test runner classes in the call stack.
 *
 * This method examines the current thread's stack trace to identify common
 * testing frameworks and runners.
 */
private fun isTestRunnerPresent(): Boolean {
    return try {
        val stackTrace = Thread.currentThread().stackTrace

        stackTrace.any { element ->
            val className = element.className.lowercase()

            // Check for JUnit runners
            className.contains("junit") ||

                    // Check for Android test frameworks
                    className.contains("robolectric") ||
                    className.contains("espresso") ||
                    className.contains("androidx.test") ||
                    className.contains("android.test") ||

                    // Check for test method names
                    element.methodName.startsWith("test") ||

                    // Check for common test class patterns
                    className.contains("test") && (
                    className.endsWith("test") ||
                            className.endsWith("tests") ||
                            className.contains("testcase")
                    )
        }
    } catch (e: Exception) {
        // If stack trace inspection fails, assume not testing
        false
    }
}