package com.ivangarzab.bark

import com.ivangarzab.bark.detectors.getCallerTag

/**
 * The purpose of this class is to serve as the main API object of this library,
 * which provides themed logging methods and configuration.
 *
 * Uses a handler-based architecture for flexible output destinations.
 *
 * @since 0.0.1
 */
object Bark {

    private val trainers = mutableListOf<Trainer>()

    private var globalTag: String? = null

    private var isMuzzled: Boolean = false

    /**
     * Log a message at VERBOSE level.
     *
     * @since 0.0.1
     */
    fun v(message: String, throwable: Throwable? = null) {
        log(Level.VERBOSE, message, throwable)
    }

    /**
     * Log a message at DEBUG level.
     *
     * @since 0.0.1
     */
    fun d(message: String, throwable: Throwable? = null) {
        log(Level.DEBUG, message, throwable)
    }

    /**
     * Log a message at INFO level.
     *
     * @since 0.0.1
     */
    fun i(message: String, throwable: Throwable? = null) {
        log(Level.INFO, message, throwable)
    }

    /**
     * Log a message at WARNING level.
     *
     * @since 0.0.1
     */
    fun w(message: String, throwable: Throwable? = null) {
        log(Level.WARNING, message, throwable)
    }

    /**
     * Log a message at ERROR level.
     *
     * @since 0.0.1
     */
    fun e(message: String, throwable: Throwable? = null) {
        log(Level.ERROR, message, throwable)
    }

    /**
     * Train Bark with a new [Trainer], which determine where and how logs are output..
     *
     * Does not accept duplicate trainer by [Pack].
     *
     * @since 0.0.1
     */
    fun train(trainer: Trainer) {
        // Remove existing trainers from the same pack
        trainers.removeAll { it.pack == trainer.pack }
        trainers.add(trainer)
    }

    /**
     * Remove a [Trainer] from the [trainers] list, and sop the system from using it.
     *
     * @since 0.0.1
     */
    fun untrain(trainer: Trainer) {
        trainers.remove(trainer)
    }

    /**
     * Muzzle Bark - disable all logging.
     *
     * @since 0.0.1
     */
    fun muzzle() {
        isMuzzled = true
    }

    /**
     * Unmuzzle Bark - re-enable logging.
     *
     * @since 0.0.1
     */
    fun unmuzzle() {
        isMuzzled = false
    }

    /**
     * Tag Bark with a global tag prefix.
     *
     * This will override tag auto-detection.
     *
     * @since 0.0.1
     */
    fun tag(tag: String) {
        globalTag = tag
    }

    /**
     * Delete the global tag prefix.
     *
     * This will re-enable tag auto-detect.
     *
     * @since 0.0.1
     */
    fun untag() {
        globalTag = null
    }

    /**
     * Internal logging method that handles level filtering and handler delegation.
     */
    private fun log(level: Level, message: String, throwable: Throwable?) {
        if (isMuzzled) return
        if (trainers.isEmpty()) return

        val tag = generateTag()
        trainers.forEach { trainers ->
            trainers.handle(level, tag, message, throwable)
        }
    }

    /**
     * Generate the tag for a particular log entry.
     */
    private fun generateTag(): String {
        return globalTag ?: getCallerTag()
    }

    /**
     * Clear all trainers.
     *
     * @since 0.0.1
     */
    fun releaseAllTrainers() {
        trainers.clear()
    }

    /**
     * Get current configuration status info.
     *
     * @since 0.0.1
     */
    fun getStatus(): String {
        return buildString {
            appendLine("Bark Status:")
            appendLine("  Muzzled: $isMuzzled")
            appendLine("  Tag: ${globalTag?.let { "[global] $it" } ?: "auto-detect"}")
            appendLine("  Trainers: ${trainers.size}")
            trainers.forEachIndexed { index, handler ->
                appendLine("    [$index] ${handler::class.simpleName}")
            }
        }
    }
}