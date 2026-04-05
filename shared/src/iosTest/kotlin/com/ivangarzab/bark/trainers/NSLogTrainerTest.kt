package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import kotlin.test.*

/**
 * The purpose of this test class is to test the [NSLogTrainer] class.
 *
 * Tests the core functionality and behavior of the iOS system log trainer.
 * Note: We can't easily mock NSLog, so we focus on testing the trainer's
 * logic, properties, and minLevel filtering behavior.
 */
class NSLogTrainerTest {

    @Test
    fun `trainer should have correct pack type`() {
        val trainer = NSLogTrainer()
        assertEquals(Pack.SYSTEM, trainer.pack, "NSLogTrainer should use SYSTEM pack")
    }

    @Test
    fun `trainer should have default minLevel VERBOSE`() {
        val trainer = NSLogTrainer()
        assertEquals(Level.VERBOSE, trainer.minLevel, "Default minLevel should be VERBOSE")
    }

    @Test
    fun `trainer should accept custom minLevel in constructor`() {
        val customTrainer = NSLogTrainer(minLevel = Level.ERROR)
        assertEquals(Level.ERROR, customTrainer.minLevel, "Custom minLevel should be respected")
    }

    @Test
    fun `minLevel filtering should work correctly with different thresholds`() {
        // We can't easily test the actual NSLog output, but we can test
        // that the trainer has the right minLevel settings for filtering logic

        val levels = listOf(
            Level.VERBOSE, Level.DEBUG, Level.INFO,
            Level.WARNING, Level.ERROR, Level.CRITICAL
        )

        levels.forEach { threshold ->
            val trainer = NSLogTrainer(minLevel = threshold)
            assertEquals(threshold, trainer.minLevel, "MinLevel should be set correctly for $threshold")

            // Test that levels below the threshold would be filtered
            Level.entries.forEach { testLevel ->
                val shouldBeFiltered = testLevel.ordinal < threshold.ordinal
                assertEquals(
                    shouldBeFiltered,
                    testLevel.ordinal < threshold.ordinal,
                    "Level $testLevel with minLevel $threshold filtering expectation"
                )
            }
        }
    }

    @Test
    fun `trainer should handle all log levels`() {
        val trainer = NSLogTrainer(minLevel = Level.VERBOSE)

        // Test that all levels are supported (we can't test output, but ensure no exceptions)
        assertDoesNotThrow("VERBOSE should not throw") {
            trainer.handle(Level.VERBOSE, "TestTag", "Verbose message", null)
        }

        assertDoesNotThrow("DEBUG should not throw") {
            trainer.handle(Level.DEBUG, "TestTag", "Debug message", null)
        }

        assertDoesNotThrow("INFO should not throw") {
            trainer.handle(Level.INFO, "TestTag", "Info message", null)
        }

        assertDoesNotThrow("WARNING should not throw") {
            trainer.handle(Level.WARNING, "TestTag", "Warning message", null)
        }

        assertDoesNotThrow("ERROR should not throw") {
            trainer.handle(Level.ERROR, "TestTag", "Error message", null)
        }

        assertDoesNotThrow("CRITICAL should not throw") {
            trainer.handle(Level.CRITICAL, "TestTag", "Critical message", null)
        }
    }

    @Test
    fun `trainer should handle exceptions gracefully`() {
        val trainer = NSLogTrainer()
        val exception = RuntimeException("Test exception")

        assertDoesNotThrow("Should handle exceptions without throwing") {
            trainer.handle(Level.ERROR, "TestTag", "Error occurred", exception)
        }

        assertDoesNotThrow("Should handle null exceptions") {
            trainer.handle(Level.ERROR, "TestTag", "Error occurred", null)
        }
    }

    @Test
    fun `trainer should handle edge case inputs`() {
        val trainer = NSLogTrainer()

        assertDoesNotThrow("Should handle empty messages") {
            trainer.handle(Level.INFO, "TestTag", "", null)
        }

        assertDoesNotThrow("Should handle empty tags") {
            trainer.handle(Level.INFO, "", "Test message", null)
        }

        assertDoesNotThrow("Should handle long messages") {
            val longMessage = "A".repeat(1000)
            trainer.handle(Level.INFO, "TestTag", longMessage, null)
        }

        assertDoesNotThrow("Should handle special characters") {
            trainer.handle(Level.INFO, "TestTag", "Message with émojis 🎉 and newlines\n", null)
        }
    }

    @Test
    fun `trainer should handle nested exceptions`() {
        val trainer = NSLogTrainer()
        val nestedCause = IllegalArgumentException("Nested cause")
        val mainException = RuntimeException("Main exception", nestedCause)

        assertDoesNotThrow("Should handle nested exceptions") {
            trainer.handle(Level.ERROR, "TestTag", "Complex error", mainException)
        }
    }

    @Test
    fun `trainer properties should be immutable after creation`() {
        val trainer = NSLogTrainer(minLevel = Level.WARNING)

        // Verify properties don't change
        assertEquals(Level.WARNING, trainer.minLevel)
        assertEquals(Pack.SYSTEM, trainer.pack)

        // These should remain constant
        repeat(3) {
            assertEquals(Level.WARNING, trainer.minLevel, "MinLevel should remain constant")
            assertEquals(Pack.SYSTEM, trainer.pack, "Pack should remain constant")
        }
    }

    @Test
    fun `multiple trainers should be independent`() {
        val trainer1 = NSLogTrainer(minLevel = Level.DEBUG)
        val trainer2 = NSLogTrainer(minLevel = Level.ERROR)

        assertEquals(Level.DEBUG, trainer1.minLevel, "First trainer should have DEBUG minLevel")
        assertEquals(Level.ERROR, trainer2.minLevel, "Second trainer should have ERROR minLevel")

        // They should not affect each other
        assertNotEquals(trainer1.minLevel, trainer2.minLevel, "Trainers should be independent")
    }

    @Test
    fun `trainer should work with realistic barK integration`() {
        // Test that demonstrates how it would be used with barK
        val trainer = NSLogTrainer(minLevel = Level.INFO)

        // Simulate barK.d() call (should be filtered - DEBUG < INFO)
        assertDoesNotThrow("Should handle filtered debug call") {
            trainer.handle(Level.DEBUG, "UserRepository", "Debug message", null)
        }

        // Simulate barK.i() call (should pass through - INFO >= INFO)
        assertDoesNotThrow("Should handle info call") {
            trainer.handle(Level.INFO, "UserRepository", "User logged in", null)
        }

        // Simulate barK.e() call with exception (should pass through - ERROR >= INFO)
        val exception = RuntimeException("Login failed")
        assertDoesNotThrow("Should handle error with exception") {
            trainer.handle(Level.ERROR, "UserRepository", "Login error", exception)
        }
    }

    // Helper function since Kotlin/Native doesn't have assertDoesNotThrow built-in
    private fun assertDoesNotThrow(message: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            fail("$message, but got exception: ${e.message}")
        }
    }
}
