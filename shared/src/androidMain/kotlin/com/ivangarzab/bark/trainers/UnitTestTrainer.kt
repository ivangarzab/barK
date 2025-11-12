package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import com.ivangarzab.bark.Trainer
import com.ivangarzab.bark.detectors.isRunningTests
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TestTrainer handles simple console logging during Kotlin tests.
 *
 * This trainer provides clean, uncolored console output during tests.
 * Perfect for environments that don't support ANSI colors,
 * or when you prefer simple clean output.
 *
 * @since 0.0.1
 * @param volume Minimum log level to output (defaults to VERBOSE - shows all)
 * @param showTimestamp Whether to include timestamps in output (defaults to true)
 */
open class UnitTestTrainer(
    override val volume: Level = Level.VERBOSE,
    private val showTimestamp: Boolean = true
) : Trainer {

    final override val pack = Pack.CONSOLE

    /**
     * Handle a log message by outputting it to plain console during tests.
     *
     * @param level The severity level of the log message
     * @param tag The tag for the log message
     * @param message The actual log message
     * @param throwable Optional throwable/exception associated with the log
     */
    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        // Only log to console when running tests
        if (!isRunningTests()) return

        // Filter based on volume setting
        if (level.ordinal < volume.ordinal) return

        // Build the formatted log message
        val formattedMessage = buildString {
            // Add timestamp if enabled
            if (showTimestamp) {
                append(getTimestamp())
                append(" ")
            }

            // Add level label (can be overridden for colors)
            append(formatLevelLabel(level))
            append(" - ")

            // Add tag
            append("$tag: ")

            // Add the actual message
            append(message)
        }

        // Print the main message
        println(formattedMessage)

        // Print exception if present
        throwable?.let {
            println(formatException(it))
            // Print stack trace for errors/critical
            if (level.ordinal >= Level.ERROR.ordinal) {
                it.printStackTrace()
            }
        }
    }

    /**
     * Format the level label - can be overridden by subclasses.
     */
    protected open fun formatLevelLabel(level: Level): String {
        return level.label
    }

    /**
     * Format exception message - can be overridden by subclasses.
     */
    protected open fun formatException(throwable: Throwable): String {
        return "Exception: ${throwable.message}"
    }

    /**
     * Format the message body - can be overridden by subclasses.
     */
    @Suppress("unused")
    protected open fun formatMessage(message: String): String {
        return message
    }

    /**
     * Get formatted timestamp for log entries.
     */
    private fun getTimestamp(): String {
        val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        return formatter.format(Date())
    }
}