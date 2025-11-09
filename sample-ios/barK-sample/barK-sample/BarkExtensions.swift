//
//  BarkExtensions.swift
//  barK-sample
//
//  Created by Ivan Garza on 8/15/25.
//

import Foundation
import shared

/**
 * Swift extensions for Bark to provide a cleaner API.
 */
extension Bark {
    /// Log a message at VERBOSE level.
    static func v(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.v(message: message, throwable: kotlinError)
    }

    /// Log a message at DEBUG level.
    static func d(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.d(message: message, throwable: kotlinError)
    }

    /// Log a message at INFO level.
    static func i(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.i(message: message, throwable: kotlinError)
    }

    /// Log a message at WARNING level.
    static func w(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.w(message: message, throwable: kotlinError)
    }

    /// Log a message at ERROR level.
    static func e(_ message: String, throwable: Error? = nil) {
        let kotlinError = throwable.map { KotlinThrowable(message: $0.localizedDescription) }
        Bark.shared.e(message: message, throwable: kotlinError)
    }

    /// Train Bark with a new Trainer.
    static func train(trainer: Trainer) {
        Bark.shared.train(trainer: trainer)
    }

    /// Remove a Trainer from the trainers list.
    static func untrain(trainer: Trainer) {
        Bark.shared.untrain(trainer: trainer)
    }

    /// Muzzle Bark - disable all logging.
    static func muzzle() {
        Bark.shared.muzzle()
    }

    /// Unmuzzle Bark - re-enable logging.
    static func unmuzzle() {
        Bark.shared.unmuzzle()
    }

    /// Tag Bark with a global tag prefix.
    static func tag(_ tag: String) {
        Bark.shared.tag(tag: tag)
    }

    /// Delete the global tag prefix.
    static func untag() {
        Bark.shared.untag()
    }

    /// Clear all trainers.
    static func releaseAllTrainers() {
        Bark.shared.releaseAllTrainers()
    }

    /// Get current configuration status info.
    static func getStatus() -> String {
        return Bark.shared.getStatus()
    }
}
