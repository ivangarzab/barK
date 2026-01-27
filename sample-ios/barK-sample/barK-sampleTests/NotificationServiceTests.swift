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

        Bark.releaseAllTrainers()
        // Start with plain trainer for contrast
        Bark.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.i("=== Setting up NotificationServiceTests ===")

        notificationService = NotificationService()

        Bark.d("NotificationService instance created")
    }

    func testAllNotificationTypesWithPlainTrainer() {
        Bark.i("Testing all notification types (Plain Output)")

        notificationService.showSuccess("User successfully created!")
        notificationService.showInfo("Data has been refreshed")
        notificationService.showWarning("Network connection is slow")
        notificationService.showError("Failed to save user preferences")

        Bark.i("✅ All notification types tested with plain trainer")
    }

    func testAllNotificationTypesWithColoredTrainer() {
        Bark.i("Switching to ColoredUnitTestTrainer for comparison")

        // Switch to colored trainer
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.i("Testing all notification types (Colored Output)")

        notificationService.showSuccess("User successfully created!")
        notificationService.showInfo("Data has been refreshed")
        notificationService.showWarning("Network connection is slow")
        notificationService.showError("Failed to save user preferences")

        Bark.i("✅ All notification types tested with colored trainer")
    }

    func testNotificationServiceInRealisticScenarios() {
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.debug, showTimestamp: true))

        Bark.i("=== Realistic Notification Scenarios ===")

        // Simulate user flow scenarios
        Bark.d("Simulating user registration flow")
        notificationService.showInfo("Starting user registration...")
        notificationService.showSuccess("Email validation passed")
        notificationService.showSuccess("User account created successfully")

        Bark.d("Simulating data synchronization")
        notificationService.showInfo("Syncing user data...")
        notificationService.showWarning("Sync taking longer than expected")
        notificationService.showSuccess("Data synchronized successfully")

        Bark.d("Simulating error recovery")
        notificationService.showError("Network request failed")
        notificationService.showInfo("Retrying request...")
        notificationService.showSuccess("Request succeeded on retry")

        Bark.i("✅ Realistic scenarios completed")
    }

    func testDemonstrateNotificationTimingAndVolumeControl() {
        Bark.i("=== Testing Volume Control and Timing ===")

        // Test with high volume level (only errors)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.error, showTimestamp: true))

        Bark.d("This debug message should NOT appear (volume = ERROR)")
        notificationService.showInfo("This info should NOT appear")
        notificationService.showWarning("This warning should NOT appear")
        notificationService.showError("This error SHOULD appear")

        Bark.i("--- Lowering volume to INFO ---")

        // Test with medium volume (INFO and above)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.info, showTimestamp: true))

        Bark.d("This debug message should NOT appear (volume = INFO)")
        notificationService.showInfo("This info SHOULD appear")
        notificationService.showWarning("This warning SHOULD appear")
        notificationService.showError("This error SHOULD appear")

        Bark.i("✅ Volume control demonstration completed")
    }

    func testCompareTrainersSideBySide() {
        Bark.i("=== Side-by-Side Trainer Comparison ===")

        let messages = [
            "Application started successfully",
            "Loading user preferences",
            "Cache miss - fetching from network",
            "API response received",
            "Data parsing completed"
        ]

        // Plain trainer first
        Bark.releaseAllTrainers()
        Bark.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.i("--- Plain UnitTestTrainer Output ---")
        messages.forEach { message in
            notificationService.showInfo(message)
        }

        // Colored trainer second
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.i("--- ColoredUnitTestTrainer Output ---")
        messages.forEach { message in
            notificationService.showInfo(message)
        }

        Bark.i("✅ Trainer comparison completed")
    }

    func testNotificationPatternsAndFrequency() {
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.i("=== Testing Notification Patterns ===")

        // Rapid fire notifications
        Bark.d("Testing rapid notifications")
        for index in 0..<5 {
            notificationService.showInfo("Rapid notification #\(index + 1)")
        }

        // Mixed severity notifications
        Bark.d("Testing mixed severity patterns")
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

        Bark.i("✅ Notification pattern testing completed")
    }
}
