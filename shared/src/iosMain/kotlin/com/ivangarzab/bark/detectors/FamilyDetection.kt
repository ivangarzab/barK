package com.ivangarzab.bark.detectors

import com.ivangarzab.bark.Trainer

/**
 * iOS platform specific trainer family detection.
 */
internal actual fun getTrainerFamily(trainer: Trainer): String? = "ios" //TODO: Implement