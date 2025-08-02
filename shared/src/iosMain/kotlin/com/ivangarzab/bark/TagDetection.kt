package com.ivangarzab.bark

/**
 * Automatically detects the calling class name for use as a log tag.
 *
 * This function uses platform-specific mechanisms to inspect the call stack
 * and determine which class/method called the logging function, allowing
 * for automatic tag generation without manual TAG constants.
 *
 * @return The simple class name of the caller, or a fallback if detection fails
 */
actual fun getCallerTag(): String = "BarK"