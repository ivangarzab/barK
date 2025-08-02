package com.ivangarzab.bark.detectors

/**
 * Test detection utilities for barK.
 *
 * Provides platform-specific detection of whether the code is currently
 * running in a test environment (unit tests, instrumented tests, etc.).
 */

/**
 * Detects if the code is currently running in a test environment.
 *
 * This function uses platform-specific mechanisms to determine if we're
 * in a testing context, which affects how barK trainers should behave.
 *
 * @return true if running tests, false otherwise
 */
internal expect fun isRunningTests(): Boolean