package com.ivangarzab.bark.detectors

import com.ivangarzab.bark.Trainer
import com.ivangarzab.bark.trainers.AndroidLogTrainer
import com.ivangarzab.bark.trainers.TestTrainer

/**
 * Platform-specific trainer family detection
 */
internal actual fun getTrainerFamily(trainer: Trainer): String? = when (trainer) {
    is AndroidLogTrainer -> Family.ANDROID.label
    is TestTrainer -> Family.TEST.label
    else -> null
}

/**
 * Family enum denoting all of the Android-specific families of [Trainer] that we can expect.
 */
internal enum class Family(val label: String) {
    ANDROID("android"),
    TEST("test")
}