package com.ivangarzab.bark.sample.android.ui.other

import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.ColoredTestTrainer
import com.ivangarzab.bark.trainers.TestTrainer
import org.junit.Before
import org.junit.Test

/**
 * Test class for NotificationService demonstrating barK's different trainer outputs
 *
 * This test showcases:
 * - Different notification types and their logging
 * - Switching between plain and colored trainers
 * - Testing logging behavior patterns
 */
class NotificationServiceTest {

    private lateinit var notificationService: NotificationService

    @Before
    fun setup() {
        Bark.releaseAllTrainers()
        // Start with plain trainer for contrast
        Bark.train(TestTrainer(volume = Level.VERBOSE, showTimestamp = false))

        Bark.i("=== Setting up NotificationServiceTest ===")

        notificationService = NotificationService()

        Bark.d("NotificationService instance created")
    }

    @Test
    fun `test all notification types with plain trainer`() {
        Bark.i("Testing all notification types (Plain Output)")

        notificationService.showSuccess("User successfully created!")
        notificationService.showInfo("Data has been refreshed")
        notificationService.showWarning("Network connection is slow")
        notificationService.showError("Failed to save user preferences")

        Bark.i("✅ All notification types tested with plain trainer")
    }

    @Test
    fun `test all notification types with colored trainer`() {
        Bark.i("Switching to ColoredTestTrainer for comparison")

        // Switch to colored trainer
        Bark.releaseAllTrainers()
        Bark.train(ColoredTestTrainer(volume = Level.VERBOSE, showTimestamp = false))

        Bark.i("Testing all notification types (Colored Output)")

        notificationService.showSuccess("User successfully created!")
        notificationService.showInfo("Data has been refreshed")
        notificationService.showWarning("Network connection is slow")
        notificationService.showError("Failed to save user preferences")

        Bark.i("✅ All notification types tested with colored trainer")
    }

    @Test
    fun `test notification service in realistic scenarios`() {
        Bark.releaseAllTrainers()
        Bark.train(ColoredTestTrainer(volume = Level.DEBUG))

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

    @Test
    fun `demonstrate notification timing and volume control`() {
        Bark.i("=== Testing Volume Control and Timing ===")

        // Test with high volume (only errors)
        Bark.releaseAllTrainers()
        Bark.train(ColoredTestTrainer(volume = Level.ERROR, showTimestamp = true))

        Bark.d("This debug message should NOT appear (volume = ERROR)")
        notificationService.showInfo("This info should NOT appear")
        notificationService.showWarning("This warning should NOT appear")
        notificationService.showError("This error SHOULD appear")

        Bark.i("--- Lowering volume to INFO ---")

        // Test with medium volume (INFO and above)
        Bark.releaseAllTrainers()
        Bark.train(ColoredTestTrainer(volume = Level.INFO, showTimestamp = true))

        Bark.d("This debug message should NOT appear (volume = INFO)")
        notificationService.showInfo("This info SHOULD appear")
        notificationService.showWarning("This warning SHOULD appear")
        notificationService.showError("This error SHOULD appear")

        Bark.i("✅ Volume control demonstration completed")
    }

    @Test
    fun `compare trainers side by side`() {
        Bark.i("=== Side-by-Side Trainer Comparison ===")

        val messages = listOf(
            "Application started successfully",
            "Loading user preferences",
            "Cache miss - fetching from network",
            "API response received",
            "Data parsing completed"
        )

        // Plain trainer first
        Bark.releaseAllTrainers()
        Bark.train(TestTrainer(volume = Level.VERBOSE, showTimestamp = true))

        Bark.i("--- Plain TestTrainer Output ---")
        messages.forEach { message ->
            notificationService.showInfo(message)
        }

        // Colored trainer second
        Bark.releaseAllTrainers()
        Bark.train(ColoredTestTrainer(volume = Level.VERBOSE, showTimestamp = true))

        Bark.i("--- ColoredTestTrainer Output ---")
        messages.forEach { message ->
            notificationService.showInfo(message)
        }

        Bark.i("✅ Trainer comparison completed")
    }

    @Test
    fun `test notification patterns and frequency`() {
        Bark.releaseAllTrainers()
        Bark.train(ColoredTestTrainer(volume = Level.VERBOSE))

        Bark.i("=== Testing Notification Patterns ===")

        // Rapid fire notifications
        Bark.d("Testing rapid notifications")
        repeat(5) { index ->
            notificationService.showInfo("Rapid notification #${index + 1}")
        }

        // Mixed severity notifications
        Bark.d("Testing mixed severity patterns")
        val severities = listOf("success", "info", "warning", "error")
        severities.forEach { severity ->
            when (severity) {
                "success" -> notificationService.showSuccess("Success: Operation $severity completed")
                "info" -> notificationService.showInfo("Info: $severity notification")
                "warning" -> notificationService.showWarning("Warning: $severity condition detected")
                "error" -> notificationService.showError("Error: $severity occurred")
            }
        }

        Bark.i("✅ Notification pattern testing completed")
    }
}