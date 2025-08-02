package com.ivangarzab.bark

/**
 * The purpose of this class is to define the different levels of logging that can be used
 * with the library.
 */
internal enum class Level(val label: String) {
    VERBOSE("[VERBOSE]"),
    DEBUG("[DEBUG]"),
    INFO("[INFO]"),
    WARNING("[WARNING]"),
    ERROR("[ERROR]"),
    CRITICAL("[CRITICAL]")
}