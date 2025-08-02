package com.ivangarzab.bark.detectors

import com.ivangarzab.bark.Trainer

/**
 * Platform-specific trainer family detection.
 */
internal expect fun getTrainerFamily(trainer: Trainer): String?