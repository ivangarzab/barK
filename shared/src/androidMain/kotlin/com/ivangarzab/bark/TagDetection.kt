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
 * first class that is not part of the barK logging framework itself.
 *
 * The detection works by:
 * 1. Getting the current stack trace
 * 2. Skipping barK framework classes (Bark, trainers, etc.)
 * 3. Finding the first "real" caller class
 * 4. Extracting and formatting the simple class name
 *
 * @return The simple class name of the caller, limited to Android's 23-char TAG limit
 */
actual fun getCallerTag(): String {
    return try {
        val stackTrace = Thread.currentThread().stackTrace

        // Find the first stack element that's not part of barK framework
        val callerElement = stackTrace.firstOrNull { element ->
            val className = element.className

            // Skip barK framework classes
            !className.contains("bark", ignoreCase = true) &&
                    !className.contains("Bark") &&
                    !className.contains("Trainer") &&

                    // Skip JVM/Android system classes
                    !className.startsWith("java.") &&
                    !className.startsWith("android.") &&
                    !className.startsWith("kotlin.") &&
                    !className.startsWith("dalvik.") &&

                    // Skip common framework classes
                    !className.contains("Thread") &&
                    !className.contains("Method") &&

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

    return withoutInnerClass.ifBlank { "App" }
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