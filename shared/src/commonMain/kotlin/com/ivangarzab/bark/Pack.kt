package com.ivangarzab.bark

/**
 * The purpose of this enum class is to serve as the categorization distinction between
 * different [Trainer] implementations, as to control how many of each kind can be
 * used by the [Bark] system.
 *
 * @since 0.0.1
 */
enum class Pack {

    /**
     * Denotes that logs will print to the native logger (e.g., android.util.log or NSLog).
     */
    SYSTEM,

    /**
     * Denotes that logs will be used on test runs only.
     */
    TEST,

    /**
     * Denotes that logs will print to a file.
     */
    FILE,

    /**
     * Denotes that logs will print to a custom destination.
     *
     * NOTE: This is the only [Pack] type that allows more than one [Trainer]
     * to be used by the [Bark] system.
     */
    CUSTOM
}