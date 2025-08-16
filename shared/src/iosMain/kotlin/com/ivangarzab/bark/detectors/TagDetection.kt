package com.ivangarzab.bark.detectors

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.darwin.*
import platform.posix.free

/**
 * iOS implementation of tag detection for barK
 *
 * Uses platform-specific mechanisms to automatically determine the calling
 * class name for use as a log tag, similar to the Android implementation.
 */

/**
 * Automatically detects the calling class name for use as a log tag on iOS
 *
 * This implementation uses several iOS-specific approaches:
 * 1. Thread call stack symbols (primary method)
 * 2. Backtrace inspection (fallback)
 * 3. Direct caller information when available
 *
 * @return The simple class name of the caller, or a fallback if detection fails
 */
internal actual fun getCallerTag(): String {
    return try {
        // Try to get caller using thread call stack symbols
        getCallerFromCallStack() ?:
        // Fallback to backtrace if available
        getCallerFromBacktrace() ?:
        // Final fallback
        "Bark"
    } catch (e: Exception) {
        // If anything goes wrong, use safe fallback
        "Bark"
    }
}

/**
 * Get caller information from iOS thread call stack symbols
 */
private fun getCallerFromCallStack(): String? {
    return try {
        // Get the current thread's call stack symbols
        val callStackSymbols = NSThread.callStackSymbols

        // Look through the call stack to find the first non-framework class
        for (i in 0 until callStackSymbols.size) {
            val symbolString = callStackSymbols[i] as? NSString ?: continue
            val symbol = symbolString.toString()

            // Parse the symbol to extract class name
            val className = parseSymbolForClassName(symbol)
            if (className != null && shouldIncludeClass(className)) {
                return extractSimpleClassName(className)
            }
        }

        null
    } catch (e: Exception) {
        null
    }
}

/**
 * Fallback method using backtrace (more low-level)
 */
@OptIn(ExperimentalForeignApi::class)
private fun getCallerFromBacktrace(): String? {
    return try {
        memScoped {
            val buffer = allocArray<COpaquePointerVar>(50) // Allocate space for 50 stack frames
            val size = backtrace(buffer, 50)
            if (size > 0) {
                val symbols = backtrace_symbols(buffer, size)
                // Look through symbols starting from frame 2 (skip getCallerTag and getCallerFromBacktrace)
                for (i in 2 until size) {
                    val symbolPtr = symbols?.get(i)
                    if (symbolPtr != null) {
                        val symbol = symbolPtr.toKString()
                        val className = parseSymbolForClassName(symbol)
                        if (className != null && shouldIncludeClass(className)) {
                            free(symbols)
                            return extractSimpleClassName(className)
                        }
                    }
                }
                free(symbols)
            }
            null
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Parse a stack trace symbol to extract the class name
 *
 * iOS symbols typically look like:
 * "1   MyApp    0x0000000100001234 MyApp.MyClass.myMethod() -> () + 123"
 * "2   MyApp    0x0000000100001234 $s5MyApp7MyClassC8myMethodyyF + 123"
 * "3   shared   0x0000000100001234 kfun:com.ivangarzab.bark.detectors.TagDetectionTest#getCallerTag(){}kotlin.String + 123"
 */
private fun parseSymbolForClassName(symbol: String): String? {
    return try {
        // Method 1: Look for Kotlin symbols with full package path
        // Example: "kfun:com.ivangarzab.bark.detectors.TagDetectionTest#method"
        val kotlinPattern = Regex("""kfun:.*\.([A-Z][A-Za-z0-9]*)[#.]""")
        val kotlinMatch = kotlinPattern.find(symbol)
        if (kotlinMatch != null) {
            return kotlinMatch.groupValues[1]
        }

        // Method 2: Look for package.ClassName.method pattern
        // Example: "com.example.MyClass.method"
        val packagePattern = Regex("""([a-z][a-z0-9_]*\.)+([A-Z][A-Za-z0-9]*)\.[a-z]""")
        val packageMatch = packagePattern.find(symbol)
        if (packageMatch != null) {
            return packageMatch.groupValues[2] // Get the ClassName part
        }

        // Method 3: Look for Swift-style patterns like "MyApp.MyClass.method"
        val swiftPattern = Regex("""([A-Z][A-Za-z0-9]*)\.([A-Z][A-Za-z0-9]*)\.[a-z]""")
        val swiftMatch = swiftPattern.find(symbol)
        if (swiftMatch != null) {
            return swiftMatch.groupValues[2] // Get the class name (second part)
        }

        // Method 4: Look for mangled Swift names (starts with $s)
        val mangledPattern = Regex("""${'$'}s\d+([A-Za-z][A-Za-z0-9]*)\d*[A-Za-z]""")
        val mangledMatch = mangledPattern.find(symbol)
        if (mangledMatch != null) {
            return mangledMatch.groupValues[1]
        }

        // Method 5: Look for Objective-C style patterns
        val objcPattern = Regex("""[-+]\[([A-Za-z][A-Za-z0-9]*)\s""")
        val objcMatch = objcPattern.find(symbol)
        if (objcMatch != null) {
            return objcMatch.groupValues[1]
        }
        null
    } catch (e: Exception) {
        null
    }
}

/**
 * Determine if a class name should be included as a valid caller
 * (i.e., filter out framework classes, barK internal classes, etc.)
 */
private fun shouldIncludeClass(className: String): Boolean {
    // Allow test classes even if they contain filtered keywords
    if (className.endsWith("Test") || className.endsWith("Tests")) {
        return true
    }

    // Skip barK framework classes (but not test classes testing them)
    if (className.endsWith("Bark") ||
        className.contains("Trainer") ||
        className.contains("TagDetection") ||
        className.contains("TestDetection")) {
        return false
    }

    // Skip iOS/Foundation framework classes
    if (className.startsWith("NS") ||
        className.startsWith("UI") ||
        className.startsWith("CF") ||
        className.startsWith("CA") ||
        className.startsWith("CG")) {
        return false
    }

    // Skip Swift standard library
    if (className.startsWith("Swift") ||
        className.startsWith("_")) {
        return false
    }

    // Skip test framework classes (but allow actual test classes)
    if (className.startsWith("XC")) {
        return false
    }

    // Must be a valid identifier
    if (className.isBlank() || !className.matches(Regex("[A-Za-z][A-Za-z0-9]*"))) {
        return false
    }

    return true
}

/**
 * Extract the simple class name from a potentially complex class identifier
 *
 * Examples:
 * - "MyApp.MainActivity" → "MainActivity"
 * - "SomeModule_SomeClass" → "SomeClass"
 * - "MyClass" → "MyClass"
 */
private fun extractSimpleClassName(fullClassName: String): String {
    // Remove module prefixes (everything before the last dot)
    val withoutModule = fullClassName.substringAfterLast('.')

    // Remove other common prefixes/suffixes
    val withoutPrefixes = withoutModule
        .removePrefix("_")
        .substringAfterLast("_")

    return withoutPrefixes.ifBlank { "Unknown" }
}