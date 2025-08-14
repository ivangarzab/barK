package com.ivangarzab.bark

/**
 * Trainer interface for handling log messages in barK
 *
 * Trainers are responsible for determining how and where log messages
 * are output. Different trainers can handle logs differently - some might
 * write to Android's Logcat, others to files, remote services, or colored
 * console output for tests.
 *
 * Trainers should implement their own level filtering if needed, as the
 * Bark object delegates all messages to all trained Trainers.
 *
 * @since 0.0.1
 */
interface Trainer {

    /**
     * The volume [Level] of the logs that will be outputted by this [Trainer].
     *
     * @since 0.0.1
     */
    val volume: Level

    /**
     * The [Pack] that this [Trainer] belongs to, which will be used to find
     * duplicative trainers and avoid duplicative log messages.
     *
     * @since 0.0.1
     */
    val pack: Pack

    /**
     * Handle a log message.
     *
     * @since 0.0.1
     * @param level The severity level of the log message
     * @param tag The tag for the log message (auto-generated or globally set)
     * @param message The actual log message
     * @param throwable Optional throwable/exception associated with the log
     */
    fun handle(level: Level, tag: String, message: String, throwable: Throwable?)
}