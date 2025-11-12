package com.ivangarzab.bark.detectors

import platform.Foundation.NSProcessInfo
import platform.posix.STDOUT_FILENO
import platform.posix.isatty

/**
 * Detects if the current environment supports ANSI color codes.
 *
 * This is crucial for iOS test output, where:
 * - Xcode's test console does NOT support ANSI colors (shows literal escape codes)
 * - Terminal/CI environments DO support ANSI colors (GitHub Actions, command line, etc.)
 *
 * Detection strategy:
 * 1. Check if output is connected to a TTY (terminal) using isatty()
 * 2. Check if TERM environment variable indicates color support
 * 3. Detect known non-color environments (Xcode test console)
 *
 * @return true if ANSI colors should be used, false otherwise
 * @since 0.2.0
 */
internal fun supportsAnsiColors(): Boolean {
    // Check if stdout is a TTY (terminal)
    // In Xcode's test console, this returns 0 (not a TTY)
    // In terminal/CI, this returns non-zero (is a TTY)
    val isTTY = isatty(STDOUT_FILENO) != 0

    // If not a TTY, definitely no color support
    if (!isTTY) {
        return false
    }

    // Check TERM environment variable
    val term = NSProcessInfo.processInfo.environment["TERM"] as? String

    // "dumb" terminal explicitly has no color support
    if (term == "dumb") {
        return false
    }

    // Common terminal types that support colors
    val colorTerminals = listOf("xterm", "xterm-256color", "screen", "vt100", "ansi", "linux")
    val supportsColors = colorTerminals.any { term?.startsWith(it) == true }

    return supportsColors
}
