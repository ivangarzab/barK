package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.detectors.supportsAnsiColors

/**
 * ColoredUnitTestTrainer extends [UnitTestTrainer] with ANSI color support for iOS.
 *
 * This trainer inherits all the functionality of [UnitTestTrainer] but adds
 * beautiful color coding for different log levels and errors,
 * when running in color-capable environments.
 *
 * Features over base [UnitTestTrainer]:
 * - ANSI color coding by log level
 * - Colored error highlighting
 * - All the same smart features (test detection, timestamps, etc.)
 * - Automatic color detection (only uses colors in compatible environments)
 *
 * **Environment Support:**
 * - ✅ Terminal/Command Line: Full color support
 * - ✅ CI/CD (GitHub Actions, etc.): Full color support
 * - ✅ Fastlane: Full color support
 * - ❌ Xcode Test Console: Falls back to plain text (Xcode limitation)
 *
 * The trainer automatically detects the environment and only outputs ANSI codes
 * when they will be properly rendered.
 * In Xcode's test console, it behaves identically to [UnitTestTrainer] (plain text output).
 *
 * @since 0.2.0
 * @param volume Minimum log level to output (defaults to [Level.VERBOSE] - shows all)
 * @param showTimestamp Whether to include timestamps in output (defaults to true)
 */
open class ColoredUnitTestTrainer(
    volume: Level = Level.VERBOSE,
    showTimestamp: Boolean = true
) : UnitTestTrainer(volume, showTimestamp) {

    /**
     * Cached result of color support detection.
     * Checked once on initialization for performance.
     */
    private val useColors: Boolean = supportsAnsiColors()

    /**
     * Override to add color formatting to the level label.
     * Only adds colors if the environment supports them.
     */
    override fun formatLevelLabel(level: Level): String {
        return if (useColors) {
            "${getColorForLevel(level)}${level.label}${ANSI_RESET}"
        } else {
            level.label
        }
    }

    /**
     * Override to add color formatting to exception messages.
     * Only adds colors if the environment supports them.
     */
    override fun formatException(throwable: Throwable): String {
        return if (useColors) {
            "${ANSI_RED}Error: ${throwable.message}${ANSI_RESET}"
        } else {
            "Error: ${throwable.message}"
        }
    }

    /**
     * Get ANSI color code for the given log level.
     */
    private fun getColorForLevel(level: Level): String {
        return when (level) {
            Level.VERBOSE -> ANSI_GRAY
            Level.DEBUG -> ANSI_BLUE
            Level.INFO -> ANSI_GREEN
            Level.WARNING -> ANSI_YELLOW
            Level.ERROR -> ANSI_RED
            Level.CRITICAL -> ANSI_BRIGHT_RED
        }
    }

    companion object {
        // ANSI color codes for console output
        private const val ANSI_RESET = "\u001B[0m"
        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_GREEN = "\u001B[32m"
        private const val ANSI_YELLOW = "\u001B[33m"
        private const val ANSI_BLUE = "\u001B[34m"
        private const val ANSI_GRAY = "\u001B[90m"
        private const val ANSI_BRIGHT_RED = "\u001B[91m"
    }
}