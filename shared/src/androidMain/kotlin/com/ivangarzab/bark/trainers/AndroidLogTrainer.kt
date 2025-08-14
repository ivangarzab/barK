package com.ivangarzab.bark.trainers

import android.util.Log
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import com.ivangarzab.bark.Trainer
import com.ivangarzab.bark.detectors.isRunningTests

/**
 * AndroidLogTrainer handles logging to Android's Logcat system.
 *
 * This trainer outputs logs using Android's built-in Log class, which makes
 * them visible in Android Studio's Logcat window and device logs.
 *
 * The trainer automatically detects test environments and disables itself
 * during tests to avoid interfering with test console output.
 *
 * @since 0.0.1
 * @param volume Minimum log level to output (defaults to [Level.VERBOSE] - shows all)
 */
open class AndroidLogTrainer(
    override val volume: Level = Level.VERBOSE
) : Trainer {

    final override val pack = Pack.SYSTEM

    /**
     * Handle a log message by outputting it to Android Logcat.
     *
     * @param level The severity level of the log message
     * @param tag The tag for the log message
     * @param message The actual log message
     * @param throwable Optional throwable/exception associated with the log
     */
    override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
        // Don't log to Android Logcat when running tests
        if (skipTests()) return
        // Filter based on volume setting
        if (level.ordinal < volume.ordinal) return

        when (level) {
            Level.VERBOSE -> Log.v(tag, message, throwable)
            Level.DEBUG -> Log.d(tag, message, throwable)
            Level.INFO -> Log.i(tag, message, throwable)
            Level.WARNING -> Log.w(tag, message, throwable)
            Level.ERROR -> Log.e(tag, message, throwable)
            Level.CRITICAL -> Log.wtf(tag, message, throwable)
        }
    }

    /**
     * Denote whether we should log while running tests or not - can be overridden by subclasses.
     *
     * @since 0.0.1
     */
    open fun skipTests(): Boolean {
        return isRunningTests()
    }
}