package com.ivangarzab.bark.detectors

/**
 * Tag detection utilities for barK.
 *
 * Provides platform-specific detection of the calling class/method name
 * for automatic tag generation, similar to Timber's behavior.
 */

/**
 * Automatically detects the calling class name for use as a log tag.
 *
 * This function uses platform-specific mechanisms to inspect the call stack
 * and determine which class/method called the logging function, allowing
 * for automatic tag generation without manual TAG constants.
 *
 * @return The simple class name of the caller, or a fallback if detection fails
 */
internal expect fun getCallerTag(): String