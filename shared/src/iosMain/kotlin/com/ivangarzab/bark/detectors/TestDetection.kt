package com.ivangarzab.bark.detectors

import platform.Foundation.NSThread

/**
 * Detects if the code is currently running in a test environment on iOS.
 *
 * This implementation uses runtime detection by examining the call stack
 * for XCTest-related classes and frameworks. This approach is reliable across
 * different build configurations and doesn't require build-time setup.
 *
 * @return true if running tests, false otherwise
 */
internal fun isRunningTests(): Boolean {
    return isXCTestPresent()
}

/**
 * Detects test environment by checking for XCTest framework in the call stack.
 *
 * This method examines the current thread's call stack symbols to identify
 * the XCTest framework and common iOS testing patterns.
 */
private fun isXCTestPresent(): Boolean {
    return try {
        val callStackSymbols = NSThread.callStackSymbols

        // Check each frame in the stack
        callStackSymbols.any { symbol ->
            val symbolString = symbol.toString().lowercase()

            // Check for XCTest framework
            symbolString.contains("xctest") ||

                    // Check for test method patterns
                    symbolString.contains("test") && (
                    symbolString.contains("testcase") ||
                            symbolString.contains("tests") ||
                            symbolString.matches(Regex(".*\\btest[a-z]+\\b.*"))
                    )
        }
    } catch (e: Exception) {
        // If stack inspection fails, assume not testing
        false
    }
}