package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level

/**
 * ColoredTestTrainer extends TestTrainer with ANSI color support.
 *
 * This trainer inherits all the functionality of TestTrainer but adds
 * beautiful color coding for different log levels and exceptions.
 *
 * Features over base TestTrainer:
 * - ANSI color coding by log level
 * - Colored exception highlighting
 * - All the same smart features (test detection, timestamps, etc.)
 *
 * @param volume Minimum log level to output (defaults to VERBOSE - shows all)
 * @param showTimestamp Whether to include timestamps in output (defaults to true)
 */
class ColoredUnitTestTrainer(
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
        return "${ANSI_RED}Exception: ${throwable.message}${ANSI_RESET}"
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