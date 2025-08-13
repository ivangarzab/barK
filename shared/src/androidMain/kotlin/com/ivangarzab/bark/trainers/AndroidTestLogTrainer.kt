package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level

/**
 * The purpose of this [AndroidLogTrainer] is to allow for the logs to be printed during
 * instrumented test runs, as well as regular Android application builds.
 */
class AndroidTestLogTrainer(
    override val volume: Level = Level.VERBOSE
) : AndroidLogTrainer() {

    // DO NOT skip test, and have this trainer work during instrumentation test runs,
    // as well as regular app runs.
    override fun skipTests(): Boolean = false
}