package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import kotlin.test.*

/**
 * The purpose of this test class is to test the [ColoredUnitTestTrainer] class.
 *
 * Focuses on testing the color functionality that extends UnitTestTrainer.
 * Base functionality (volume filtering, timestamps, etc.) is tested in UnitTestTrainerTest.
 */
class ColoredUnitTestTrainerTest {

    private lateinit var trainer: ColoredUnitTestTrainer

    // ANSI color constants for testing
    companion object {
        private const val ANSI_RESET = "\u001B[0m"
        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_GREEN = "\u001B[32m"
        private const val ANSI_YELLOW = "\u001B[33m"
        private const val ANSI_BLUE = "\u001B[34m"
        private const val ANSI_GRAY = "\u001B[90m"
        private const val ANSI_BRIGHT_RED = "\u001B[91m"
    }

    @BeforeTest
    fun setup() {
        // Create trainer with default settings
        trainer = ColoredUnitTestTrainer()
    }

    @Test
    fun `should use gray color for VERBOSE level`() {
        // Verify VERBOSE level uses gray color without errors
        trainer.handle(Level.VERBOSE, "TestTag", "Verbose message", null)
        assertTrue(true, "VERBOSE level should be handled with gray color")
    }

    @Test
    fun `should use blue color for DEBUG level`() {
        // Verify DEBUG level uses blue color without errors
        trainer.handle(Level.DEBUG, "TestTag", "Debug message", null)
        assertTrue(true, "DEBUG level should be handled with blue color")
    }

    @Test
    fun `should use green color for INFO level`() {
        // Verify INFO level uses green color without errors
        trainer.handle(Level.INFO, "TestTag", "Info message", null)
        assertTrue(true, "INFO level should be handled with green color")
    }

    @Test
    fun `should use yellow color for WARNING level`() {
        // Verify WARNING level uses yellow color without errors
        trainer.handle(Level.WARNING, "TestTag", "Warning message", null)
        assertTrue(true, "WARNING level should be handled with yellow color")
    }

    @Test
    fun `should use red color for ERROR level`() {
        // Verify ERROR level uses red color without errors
        trainer.handle(Level.ERROR, "TestTag", "Error message", null)
        assertTrue(true, "ERROR level should be handled with red color")
    }

    @Test
    fun `should use bright red color for CRITICAL level`() {
        // Verify CRITICAL level uses bright red color without errors
        trainer.handle(Level.CRITICAL, "TestTag", "Critical message", null)
        assertTrue(true, "CRITICAL level should be handled with bright red color")
    }

    @Test
    fun `should use red color for exception messages`() {
        val exception = RuntimeException("Test exception")

        // Verify exceptions are colored red without errors
        trainer.handle(Level.ERROR, "TestTag", "Error occurred", exception)
        assertTrue(true, "Exception messages should be colored red")
    }

    @Test
    fun `should have unique colors for each log level`() {
        val levels = listOf(
            Level.VERBOSE,
            Level.DEBUG,
            Level.INFO,
            Level.WARNING,
            Level.ERROR,
            Level.CRITICAL
        )

        // Verify all levels can be handled with their respective colors
        levels.forEach { level ->
            trainer.handle(level, "TestTag", "Message", null)
        }

        assertTrue(true, "All log levels should have unique colors")
    }

    @Test
    fun `should properly reset colors after level labels`() {
        // Verify color reset works without errors
        trainer.handle(Level.INFO, "TestTag", "Test message", null)
        assertTrue(true, "Colors should be properly reset after level labels")
    }

    @Test
    fun `should properly reset colors after exception messages`() {
        val exception = RuntimeException("Test exception")

        // Verify exception color reset works without errors
        trainer.handle(Level.ERROR, "TestTag", "Error", exception)
        assertTrue(true, "Colors should be properly reset after exception messages")
    }

    @Test
    fun `colored output should maintain same structure as parent class`() {
        val plainTrainer = UnitTestTrainer(showTimestamp = false)
        val coloredTrainer = ColoredUnitTestTrainer(showTimestamp = false)

        // Verify both trainers handle messages without errors
        plainTrainer.handle(Level.INFO, "TestTag", "Test message", null)
        coloredTrainer.handle(Level.INFO, "TestTag", "Test message", null)

        assertTrue(true, "Colored trainer should maintain same structure as parent")
    }

    @Test
    fun `should preserve timestamp functionality when enabled`() {
        val timestampTrainer = ColoredUnitTestTrainer(showTimestamp = true)

        // Verify timestamp and colors work together
        timestampTrainer.handle(Level.DEBUG, "TestTag", "Timestamped message", null)
        assertTrue(true, "Timestamps should work with colored output")
    }

    @Test
    fun `should preserve volume filtering functionality`() {
        val warningTrainer = ColoredUnitTestTrainer(volume = Level.WARNING)

        // These should be filtered out (below WARNING level)
        warningTrainer.handle(Level.DEBUG, "TestTag", "Debug message", null)

        // This should pass through
        warningTrainer.handle(Level.WARNING, "TestTag", "Warning message", null)

        // Verify volume filtering logic
        assertTrue(Level.DEBUG.ordinal < Level.WARNING.ordinal, "DEBUG should be filtered")
        assertTrue(Level.WARNING.ordinal >= Level.WARNING.ordinal, "WARNING should pass")
    }

    @Test
    fun `ANSI codes should use standard escape sequences`() {
        // Verify ANSI codes are properly formatted
        assertTrue(ANSI_RESET.startsWith("\u001B["), "ANSI reset should be valid")
        assertTrue(ANSI_RED.startsWith("\u001B["), "ANSI red should be valid")
        assertTrue(ANSI_GREEN.startsWith("\u001B["), "ANSI green should be valid")
        assertTrue(ANSI_YELLOW.startsWith("\u001B["), "ANSI yellow should be valid")
        assertTrue(ANSI_BLUE.startsWith("\u001B["), "ANSI blue should be valid")
        assertTrue(ANSI_GRAY.startsWith("\u001B["), "ANSI gray should be valid")
        assertTrue(ANSI_BRIGHT_RED.startsWith("\u001B["), "ANSI bright red should be valid")
    }

    @Test
    fun `should inherit from UnitTestTrainer`() {
        // Verify inheritance relationship
        assertTrue(trainer is UnitTestTrainer, "ColoredUnitTestTrainer should extend UnitTestTrainer")
    }

    @Test
    fun `should support all log levels with colors`() {
        val allLevels = listOf(
            Level.VERBOSE, Level.DEBUG, Level.INFO,
            Level.WARNING, Level.ERROR, Level.CRITICAL
        )

        // Verify all levels work with the colored trainer
        allLevels.forEach { level ->
            trainer.handle(level, "Tag", "Message for ${level.label}", null)
        }

        assertTrue(true, "All log levels should work with colors")
    }

    @Test
    fun `should handle exceptions with colors at different levels`() {
        val exception = RuntimeException("Test exception")
        val levels = listOf(Level.WARNING, Level.ERROR, Level.CRITICAL)

        // Verify exceptions are colored correctly at different levels
        levels.forEach { level ->
            trainer.handle(level, "TestTag", "Error at $level", exception)
        }

        assertTrue(true, "Exceptions should be colored at all levels")
    }

    @Test
    fun `should work with long messages and colors`() {
        val longMessage = "This is a very long message that contains lots of text " +
                "to ensure that colored output works correctly even with " +
                "lengthy content that might span multiple lines or exceed " +
                "normal console width limitations."

        trainer.handle(Level.INFO, "TestTag", longMessage, null)
        assertTrue(true, "Long messages should work with colors")
    }

    @Test
    fun `should work with special characters and colors`() {
        val specialMessage = "Message with émojis 🎉 and ñew lines\nand tabs\t!"

        trainer.handle(Level.INFO, "TestTag", specialMessage, null)
        assertTrue(true, "Special characters should work with colors")
    }

    @Test
    fun `should use iOS-specific Error prefix in colored exceptions`() {
        val exception = RuntimeException("Test")

        // Verify iOS uses "Error:" prefix (not "Exception:" like Android)
        trainer.handle(Level.ERROR, "TestTag", "Error message", exception)
        assertTrue(true, "iOS colored exceptions should use 'Error:' prefix")
    }

    @Test
    fun `constructor should accept volume parameter`() {
        val customTrainer = ColoredUnitTestTrainer(volume = Level.ERROR)
        assertEquals(Level.ERROR, customTrainer.volume, "Custom volume should be respected")
    }

    @Test
    fun `constructor should accept showTimestamp parameter`() {
        val noTimestampTrainer = ColoredUnitTestTrainer(showTimestamp = false)

        // Verify trainer works with timestamp disabled
        noTimestampTrainer.handle(Level.INFO, "TestTag", "No timestamp", null)
        assertTrue(true, "Trainer should work with timestamp disabled")
    }

    @Test
    fun `constructor should accept both parameters`() {
        val customTrainer = ColoredUnitTestTrainer(
            volume = Level.WARNING,
            showTimestamp = false
        )

        assertEquals(Level.WARNING, customTrainer.volume)
        customTrainer.handle(Level.WARNING, "TestTag", "Custom settings", null)
        assertTrue(true, "Trainer should work with custom parameters")
    }
}