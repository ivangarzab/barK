package com.ivangarzab.bark

/**
 * iOS platform specific trainer family detection.
 */
internal actual fun getTrainerFamily(trainer: Trainer): String? = "ios" //TODO: Implement