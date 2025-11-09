package com.ivangarzab.bark

/**
 * iOS-specific configuration for barK.
 *
 * This object is only available on iOS and provides platform-specific settings
 * that don't apply to other platforms like Android.
 *
 * @since 0.2.0
 */
object BarkConfig {
    /**
     * Enable or disable automatic tag detection from stack traces on iOS.
     *
     * This option applies globally, and to all iOS trainers.
     *
     * When true, (the default) barK will not auto-detect the calling class, and depending on
     * a global tag as fallback..
     *
     * When false, barK will automatically detect the calling class name
     * and use it as the log tag, or fallback to "BarK.".
     *
     * **WARNING:** Auto-detection involves parsing iOS stack traces on every
     * log call, which has a small performance cost. If you're logging
     * in performance-critical code paths, consider disabling this feature.
     *
     * @since 0.2.0
     */
    var autoTagDisabled: Boolean = true
}
