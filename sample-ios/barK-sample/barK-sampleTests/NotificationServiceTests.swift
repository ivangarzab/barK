//
//  NotificationServiceTests.swift
//  barK-sampleTests
//

import XCTest
import shared
@testable import barK_sample

/**
 * Test class for NotificationService demonstrating barK's different trainer outputs
 *
 * This test showcases:
 * - Different notification types and their logging
 * - Switching between plain and colored trainers
 * - Testing logging behavior patterns
 */
final class NotificationServiceTests: XCTestCase {

    private var notificationService: NotificationService!

    override func setUp() {
        super.setUp()

        Bark.shared.releaseAllTrainers()
        // Start with plain trainer for contrast
        Bark.shared.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.i(message: "=== Setting up NotificationServiceTests ===", throwable: nil)

        notificationService = NotificationService()

        Bark.shared.d(message: "NotificationService instance created", throwable: nil)
    }

    func testAllNotificationTypesWithPlainTrainer() {
        Bark.shared.i(message: "Testing all notification types (Plain Output)", throwable: nil)

        notificationService.showSuccess("User successfully created!")
        notificationService.showInfo("Data has been refreshed")
        notificationService.showWarning("Network connection is slow")
        notificationService.showError("Failed to save user preferences")

        Bark.shared.i(message: "✅ All notification types tested with plain trainer", throwable: nil)
    }

    func testAllNotificationTypesWithColoredTrainer() {
        Bark.shared.i(message: "Switching to ColoredUnitTestTrainer for comparison", throwable: nil)

        // Switch to colored trainer
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.i(message: "Testing all notification types (Colored Output)", throwable: nil)

        notificationService.showSuccess("User successfully created!")
        notificationService.showInfo("Data has been refreshed")
        notificationService.showWarning("Network connection is slow")
        notificationService.showError("Failed to save user preferences")

        Bark.shared.i(message: "✅ All notification types tested with colored trainer", throwable: nil)
    }

    func testNotificationServiceInRealisticScenarios() {
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.debug, showTimestamp: true))

        Bark.shared.i(message: "=== Realistic Notification Scenarios ===", throwable: nil)

        // Simulate user flow scenarios
        Bark.shared.d(message: "Simulating user registration flow", throwable: nil)
        notificationService.showInfo("Starting user registration...")
        notificationService.showSuccess("Email validation passed")
        notificationService.showSuccess("User account created successfully")

        Bark.shared.d(message: "Simulating data synchronization", throwable: nil)
        notificationService.showInfo("Syncing user data...")
        notificationService.showWarning("Sync taking longer than expected")
        notificationService.showSuccess("Data synchronized successfully")

        Bark.shared.d(message: "Simulating error recovery", throwable: nil)
        notificationService.showError("Network request failed")
        notificationService.showInfo("Retrying request...")
        notificationService.showSuccess("Request succeeded on retry")

        Bark.shared.i(message: "✅ Realistic scenarios completed", throwable: nil)
    }

    func testDemonstrateNotificationTimingAndVolumeControl() {
        Bark.shared.i(message: "=== Testing Volume Control and Timing ===", throwable: nil)

        // Test with high volume level (only errors)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.error, showTimestamp: true))

        Bark.shared.d(message: "This debug message should NOT appear (volume = ERROR)", throwable: nil)
        notificationService.showInfo("This info should NOT appear")
        notificationService.showWarning("This warning should NOT appear")
        notificationService.showError("This error SHOULD appear")

        Bark.shared.i(message: "--- Lowering volume to INFO ---", throwable: nil)

        // Test with medium volume (INFO and above)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.info, showTimestamp: true))

        Bark.shared.d(message: "This debug message should NOT appear (volume = INFO)", throwable: nil)
        notificationService.showInfo("This info SHOULD appear")
        notificationService.showWarning("This warning SHOULD appear")
        notificationService.showError("This error SHOULD appear")

        Bark.shared.i(message: "✅ Volume control demonstration completed", throwable: nil)
    }

    func testCompareTrainersSideBySide() {
        Bark.shared.i(message: "=== Side-by-Side Trainer Comparison ===", throwable: nil)

        let messages = [
            "Application started successfully",
            "Loading user preferences",
            "Cache miss - fetching from network",
            "API response received",
            "Data parsing completed"
        ]

        // Plain trainer first
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.shared.i(message: "--- Plain UnitTestTrainer Output ---", throwable: nil)
        messages.forEach { message in
            notificationService.showInfo(message)
        }

        // Colored trainer second
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.shared.i(message: "--- ColoredUnitTestTrainer Output ---", throwable: nil)
        messages.forEach { message in
            notificationService.showInfo(message)
        }

        Bark.shared.i(message: "✅ Trainer comparison completed", throwable: nil)
    }

    func testNotificationPatternsAndFrequency() {
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.i(message: "=== Testing Notification Patterns ===", throwable: nil)

        // Rapid fire notifications
        Bark.shared.d(message: "Testing rapid notifications", throwable: nil)
        for index in 0..<5 {
            notificationService.showInfo("Rapid notification #\(index + 1)")
        }

        // Mixed severity notifications
        Bark.shared.d(message: "Testing mixed severity patterns", throwable: nil)
        let severities = ["success", "info", "warning", "error"]
        severities.forEach { severity in
            switch severity {
            case "success":
                notificationService.showSuccess("Success: Operation \(severity) completed")
            case "info":
                notificationService.showInfo("Info: \(severity) notification")
            case "warning":
                notificationService.showWarning("Warning: \(severity) condition detected")
            case "error":
                notificationService.showError("Error: \(severity) occurred")
            default:
                break
            }
        }

        Bark.shared.i(message: "✅ Notification pattern testing completed", throwable: nil)
    }
}
