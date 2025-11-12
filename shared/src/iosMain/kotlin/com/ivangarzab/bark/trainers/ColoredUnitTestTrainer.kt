package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level

/**
 * ColoredUnitTestTrainer extends UnitTestTrainer with ANSI color support for iOS.
 *
 * This trainer inherits all the functionality of UnitTestTrainer but adds
 * beautiful color coding for different log levels and exceptions in the
 * Xcode console during test runs.
 *
 * Features over base UnitTestTrainer:
 * - ANSI color coding by log level
 * - Colored exception highlighting
 * - All the same smart features (test detection, timestamps, etc.)
 *
 * Note: ANSI colors work in Xcode's console output during test runs,
 * similar to how they work in Android Studio's console (not in production logs).
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
     * Override to add color formatting to the level label.
     */
    override fun formatLevelLabel(level: Level): String {
        return "${getColorForLevel(level)}${level.label}${ANSI_RESET}"
    }

    /**
     * Override to add color formatting to exception messages.
     */
    override fun formatException(throwable: Throwable): String {
        return "${ANSI_RED}Error: ${throwable.message}${ANSI_RESET}"
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