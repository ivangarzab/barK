package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import com.ivangarzab.bark.Trainer
import com.ivangarzab.bark.detectors.isRunningTests
import platform.Foundation.*

/**
 * The purpose of this class is to handle logging to iOS system logs using [NSLog].
 *
 * This trainer outputs logs using NSLog, which makes them visible in
 * Console.app, Xcode console, and system logs. The system automatically
 * adds timestamp, app name, process ID, and thread information.
 *
 * @since 0.2.0
 * @param minLevel Minimum log level to output
 */
class NSLogTrainer(
    override val minLevel: Level = Level.VERBOSE
) : Trainer {

    override val pack = Pack.SYSTEM

    /**
     * Handle a log message by outputting it to iOS system logs.
     *
     * @param level The severity level of the log message
     * @param tag The tag for the log message
     * @param message The actual log message
     * @param throwable Optional throwable/exception associated with the log
     */
    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        // Only log to console when NOT running tests
        if (isRunningTests()) return

        // Filter based on minLevel
        if (level.ordinal < minLevel.ordinal) return

        val formattedMessage = buildString {
            append("${level.label} ${if (tag.isNotEmpty()) "$tag: " else ""}$message")

            throwable?.let {
                append(" | Exception: ${it.message}")
                if (level.ordinal >= Level.ERROR.ordinal) {
                    append(" | ${it.stackTraceToString()}")
                }
            }
        }

        NSLog(formattedMessage)
    }
}