//
//  BarkExtensions.swift
//  barK
//
//  Swift extensions for a cleaner barK API on iOS.
//  Copy this file to your iOS project for idiomatic Swift usage.
//
//  Usage:
//    Instead of: Bark.shared.i(message: "Hello", throwable: nil)
//    Use:        Bark.i("Hello")
//
// NOTE: Make sure to update this file if ./ios/BarkExtensions changes!

import Foundation
import shared

/**
 * Swift extensions for Bark to provide a cleaner, more idiomatic API.
 *
 * These extensions allow you to call Bark methods without the `.shared` prefix
 * and with more natural Swift syntax.
 */
extension Bark {
    /// Log a message at VERBOSE level.
    ///
    /// - Parameters:
    ///   - message: The log message
    ///   - throwable: Optional Swift Error to log (automatically converted to KotlinThrowable)
    public static func v(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.v(message: message, throwable: kotlinError)
    }

    /// Log a message at DEBUG level.
    ///
    /// - Parameters:
    ///   - message: The log message
    ///   - throwable: Optional Swift Error to log (automatically converted to KotlinThrowable)
    public static func d(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.d(message: message, throwable: kotlinError)
    }

    /// Log a message at INFO level.
    ///
    /// - Parameters:
    ///   - message: The log message
    ///   - throwable: Optional Swift Error to log (automatically converted to KotlinThrowable)
    public static func i(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.i(message: message, throwable: kotlinError)
    }

    /// Log a message at WARNING level.
    ///
    /// - Parameters:
    ///   - message: The log message
    ///   - throwable: Optional Swift Error to log (automatically converted to KotlinThrowable)
    public static func w(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.w(message: message, throwable: kotlinError)
    }

    /// Log a message at ERROR level.
    ///
    /// - Parameters:
    ///   - message: The log message
    ///   - throwable: Optional Swift Error to log (automatically converted to KotlinThrowable)
    public static func e(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.e(message: message, throwable: kotlinError)
    }

    /// Train Bark with a new Trainer.
    ///
    /// - Parameter trainer: The Trainer to add
    public static func train(trainer: Trainer) {
        Bark.shared.train(trainer: trainer)
    }

    /// Remove a Trainer from the trainers list.
    ///
    /// - Parameter trainer: The Trainer to remove
    public static func untrain(trainer: Trainer) {
        Bark.shared.untrain(trainer: trainer)
    }

    /// Clear all trainers.
    public static func releaseAllTrainers() {
        Bark.shared.releaseAllTrainers()
    }

    /// Muzzle Bark - disable all logging.
    public static func muzzle() {
        Bark.shared.muzzle()
    }

    /// Unmuzzle Bark - re-enable logging.
    public static func unmuzzle() {
        Bark.shared.unmuzzle()
    }

    /// Tag Bark with a global tag prefix.
    ///
    /// This will override automatic tag detection.
    ///
    /// - Parameter tag: The tag prefix to use
    public static func tag(_ tag: String) {
        Bark.shared.tag(tag: tag)
    }

    /// Delete the global tag prefix.
    ///
    /// This will re-enable automatic tag detection.
    public static func untag() {
        Bark.shared.untag()
    }

    /// Get current configuration status info.
    ///
    /// - Returns: A string describing the current Bark configuration
    public static func getStatus() -> String {
        return Bark.shared.getStatus()
    }

    /// Enable/disable automatic tag detection from stack traces (iOS only).
    ///
    /// When `false`, barK automatically detects the calling class name from the stack trace.
    /// When `true` (default), auto-detection is disabled and a global tag is used as fallback.
    ///
    /// **Warning:** Auto-detection has a small performance cost. Consider disabling
    /// if logging in performance-critical code paths.
    public static var autoTagDisabled: Bool {
        get {
            return BarkConfig.shared.autoTagDisabled
        }
        set {
            BarkConfig.shared.autoTagDisabled = newValue
        }
    }
}