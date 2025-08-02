package com.ivangarzab.bark

/**
 * Platform-specific trainer family detection.
 */
internal expect fun getTrainerFamily(trainer: Trainer): String?