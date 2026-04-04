package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack

/**
 * The purpose of this [AndroidLogTrainer] is to allow for the logs to be printed during
 * instrumented test runs via Android Logcat.
 *
 * Unlike [AndroidLogTrainer] (Pack.SYSTEM, skips tests), this trainer is categorized
 * as Pack.TEST and is designed specifically for use during instrumented test runs.
 *
 * @since 0.0.1
 * @param volume Minimum log level to output (defaults to [Level.VERBOSE] - shows all)
 */
class AndroidTestLogTrainer(
    override val volume: Level = Level.VERBOSE
) : AndroidLogTrainer() {

    override val pack = Pack.TEST

    // DO NOT skip tests — this trainer exists specifically for instrumented test runs.
    override fun skipTests(): Boolean = false
}