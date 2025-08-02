package com.ivangarzab.bark

/**
 * The purpose of this class is to serve as the main API object of this library,
 * which provides themed logging methods and configuration.
 *
 * Uses a handler-based architecture for flexible output destinations.
 */
object Bark {

    private val trainers = mutableListOf<Trainer>()

    private var globalTag: String? = null

    private var isMuzzled: Boolean = false

    /**
     * Log a message at VERBOSE level.
     */
    fun v(message: String, throwable: Throwable? = null) {
        log(Level.VERBOSE, message, throwable)
    }

    /**
     * Log a message at DEBUG level.
     */
    fun d(message: String, throwable: Throwable? = null) {
        log(Level.DEBUG, message, throwable)
    }

    /**
     * Log a message at INFO level.
     */
    fun i(message: String, throwable: Throwable? = null) {
        log(Level.INFO, message, throwable)
    }

    /**
     * Log a message at WARNING level.
     */
    fun w(message: String, throwable: Throwable? = null) {
        log(Level.WARNING, message, throwable)
    }

    /**
     * Log a message at ERROR level.
     */
    fun e(message: String, throwable: Throwable? = null) {
        log(Level.ERROR, message, throwable)
    }

    /**
     * Train Bark with a new handler.
     *
     * Handlers determine where and how logs are output.
     */
    fun train(handler: Trainer) {
        trainers.add(handler)
    }

    /**
     * Muzzle Bark - disable all logging.
     */
    fun muzzle() {
        isMuzzled = true
    }

    /**
     * Unmuzzle Bark - re-enable logging.
     */
    fun unmuzzle() {
        isMuzzled = false
    }

    /**
     * Tag Bark with a global tag prefix.
     *
     * This will override tag auto-detection.
     */
    fun tag(tag: String) {
        globalTag = tag
    }

    /**
     * Delete the global tag prefix.
     *
     * This will re-enable tag auto-detect.
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
     */
    fun releaseAllTrainers() {
        trainers.clear()
    }

    /**
     * Get current configuration status info.
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