package com.ivangarzab.bark

/**
 * The purpose of this enum class is to serve as the categorization distinction between
 * different [Trainer] implementations, as to control how many of each kind can be
 * used by the [Bark] system.
 */
enum class Pack {

    /** Denotes that logs will print to the native console (println() / print()).*/
    CONSOLE,

    /** Denotes that logs will print to the native logger (Log.d() / os_log()).*/
    SYSTEM,

    /** Denotes that logs will print to a file.*/
    FILE,

    /** Denotes that logs will print to a custom destination.*/
    CUSTOM
}