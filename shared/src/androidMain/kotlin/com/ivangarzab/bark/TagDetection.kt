package com.ivangarzab.bark

/**
 * Android implementation of tag detection for barK
 *
 * Uses Java stack trace inspection to automatically determine the calling
 * class name for use as a log tag, mimicking Timber's auto-tag behavior.
 */

/**
 * Automatically detects the calling class name for use as a log tag on Android
 *
 * This implementation inspects the current thread's stack trace to find the
 * first class that is not part of barK framework or common system classes.
 *
 * Inspired by Timber's approach but adapted for barK.
 *
 * @return The simple class name of the caller, limited to Android's 23-char TAG limit
 */
actual fun getCallerTag(): String {
    return try {
        val stackTrace = Thread.currentThread().stackTrace

        // Find the first stack element that's not part of framework classes
        val callerElement = stackTrace.firstOrNull { element ->
            val className = element.className

            // Skip barK framework classes (but not test classes!)
            !className.endsWith("Bark") &&
                    !className.contains("Trainer") &&
                    !className.endsWith("TagDetection") &&  // Skip the actual TagDetection file
                    !className.endsWith("TagDetectionKt") &&  // Skip the compiled Kotlin file
                    !className.endsWith("TestDetection") &&  // Skip the actual TestDetection file
                    !className.endsWith("TestDetectionKt") &&  // Skip the compiled Kotlin file

                    // Skip JVM/Android system classes
                    !className.startsWith("java.") &&
                    !className.startsWith("android.") &&
                    !className.startsWith("kotlin.") &&
                    !className.startsWith("dalvik.") &&
                    !className.startsWith("jdk.") &&

                    // Skip testing infrastructure (but not test classes themselves!)
                    !className.startsWith("org.junit") &&
                    !className.contains("junit.runners") &&
                    !className.contains("gradle") &&
                    !className.contains("Reflective") &&
                    !className.contains("Framework") &&
                    !className.contains("Runner") &&
                    !className.contains("Callable") &&
                    !className.contains("Method.invoke") &&

                    // Skip proxy classes
                    !className.contains("Proxy") &&
                    !className.contains("$\$") &&
                    !className.contains("Lambda") &&

                    // Make sure it's a real class name
                    className.isNotBlank()
        }

        if (callerElement != null) {
            // Extract simple class name from full class path
            val fullClassName = callerElement.className
            val simpleClassName = extractSimpleClassName(fullClassName)

            // Apply Android's 23-character TAG limit
            truncateToTagLimit(simpleClassName)
        } else {
            // Fallback if we can't detect the caller
            "Bark"
        }

    } catch (e: Exception) {
        // If anything goes wrong, use safe fallback
        "Bark"
    }
}

/**
 * Extract the simple class name from a fully qualified class name
 *
 * Examples:
 * - "com.example.app.MainActivity" → "MainActivity"
 * - "com.example.app.MainActivity$Companion" → "MainActivity"
 * - "com.example.app.MainActivity$1" → "MainActivity"
 */
private fun extractSimpleClassName(fullClassName: String): String {
    // Remove package names (everything before the last dot)
    val withoutPackage = fullClassName.substringAfterLast('.')

    // Remove inner class markers (everything after $)
    val withoutInnerClass = withoutPackage.substringBefore('$')

    return withoutInnerClass.ifBlank { "Unknown" }
}

/**
 * Truncate tag to Android's 23-character limit while preserving readability
 *
 * If the tag is longer than 23 characters, we try to keep it readable by:
 * 1. Using the full name if it fits
 * 2. Truncating but keeping the end (most specific part)
 * 3. Adding indicator that it was truncated
 */
private fun truncateToTagLimit(tag: String): String {
    val maxLength = 23

    return when {
        tag.length <= maxLength -> tag
        else -> {
            // Take the last part to keep the most specific information
            // Reserve 1 character for truncation indicator
            val truncated = tag.takeLast(maxLength - 1)
            "*$truncated" // * indicates truncation
        }
    }
}