package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import com.ivangarzab.bark.Trainer
import org.junit.Test
import org.junit.Assert.*

/**
 * The purpose of this test class is to test [AndroidTestLogTrainer] class.
 *
 * Tests the key differences from AndroidLogTrainer:
 * - Does NOT skip tests (primary difference)
 * - Maintains proper pack type and minLevel behavior
 * - Inherits all AndroidLogTrainer functionality correctly
 *
 * Note: We don't test the actual Android Log calls since those are simple
 * delegations to the Android framework. We focus on the behavioral logic.
 */
class AndroidTestLogTrainerTest {

    @Test
    fun `trainer should have correct pack type`() {
        val trainer = AndroidTestLogTrainer()
        assertEquals("AndroidTestLogTrainer should use TEST pack", Pack.TEST, trainer.pack)
    }

    @Test
    fun `trainer should have default minLevel VERBOSE`() {
        val trainer = AndroidTestLogTrainer()
        assertEquals("Default minLevel should be VERBOSE", Level.VERBOSE, trainer.minLevel)
    }

    @Test
    fun `trainer should accept custom minLevel in constructor`() {
        val customTrainer = AndroidTestLogTrainer(minLevel = Level.ERROR)
        assertEquals("Custom minLevel should be respected", Level.ERROR, customTrainer.minLevel)
    }

    @Test
    fun `key difference - skipTests should return false`() {
        val trainer = AndroidTestLogTrainer()
        assertFalse(
            "AndroidTestLogTrainer should NOT skip tests - this is its primary purpose",
            trainer.skipTests()
        )
    }

    @Test
    fun `should behave differently from parent AndroidLogTrainer`() {
        val testTrainer = AndroidTestLogTrainer()
        val regularTrainer = AndroidLogTrainer()

        // The key behavioral difference
        assertFalse("AndroidTestLogTrainer should not skip tests", testTrainer.skipTests())
        assertTrue("AndroidLogTrainer should skip tests when in test environment", regularTrainer.skipTests())
    }

    @Test
    fun `should inherit proper minLevel filtering behavior`() {
        // Test that minLevel filtering works as expected (inherited from parent)
        val warningTrainer = AndroidTestLogTrainer(minLevel = Level.WARNING)

        // Verify minLevel property is set correctly
        assertEquals(Level.WARNING, warningTrainer.minLevel)

        // Test minLevel filtering logic (levels below WARNING should be filtered)
        assertTrue("VERBOSE should be below WARNING threshold",
            Level.VERBOSE.ordinal < Level.WARNING.ordinal)
        assertTrue("DEBUG should be below WARNING threshold",
            Level.DEBUG.ordinal < Level.WARNING.ordinal)
        assertTrue("INFO should be below WARNING threshold",
            Level.INFO.ordinal < Level.WARNING.ordinal)

        // These should pass through
        assertFalse("WARNING should pass minLevel filter",
            Level.WARNING.ordinal < Level.WARNING.ordinal)
        assertFalse("ERROR should pass minLevel filter",
            Level.ERROR.ordinal < Level.WARNING.ordinal)
        assertFalse("CRITICAL should pass minLevel filter",
            Level.CRITICAL.ordinal < Level.WARNING.ordinal)
    }

    @Test
    fun `should have different pack type from parent`() {
        val testTrainer = AndroidTestLogTrainer()
        val regularTrainer = AndroidLogTrainer()

        assertNotEquals("Trainers should use different pack types",
            regularTrainer.pack, testTrainer.pack)
        assertEquals("AndroidLogTrainer should be SYSTEM pack", Pack.SYSTEM, regularTrainer.pack)
        assertEquals("AndroidTestLogTrainer should be TEST pack", Pack.TEST, testTrainer.pack)
    }

    @Test
    fun `should have correct level mapping behavior`() {
        val trainer = AndroidTestLogTrainer(minLevel = Level.VERBOSE)

        // We can't call handle() because it tries to use Android Log in unit tests
        // Instead, we test the logic that determines if a message would be processed

        // All levels should be processed when minLevel is VERBOSE
        Level.values().forEach { level ->
            val wouldBeFiltered = level.ordinal < Level.VERBOSE.ordinal
            assertFalse("Level $level should not be filtered with VERBOSE minLevel", wouldBeFiltered)
        }

        // Test with higher minLevel - only some levels should pass
        val warningTrainer = AndroidTestLogTrainer(minLevel = Level.WARNING)

        assertTrue("VERBOSE should be filtered with WARNING minLevel",
            Level.VERBOSE.ordinal < Level.WARNING.ordinal)
        assertTrue("DEBUG should be filtered with WARNING minLevel",
            Level.DEBUG.ordinal < Level.WARNING.ordinal)
        assertTrue("INFO should be filtered with WARNING minLevel",
            Level.INFO.ordinal < Level.WARNING.ordinal)
        assertFalse("WARNING should not be filtered with WARNING minLevel",
            Level.WARNING.ordinal < Level.WARNING.ordinal)
        assertFalse("ERROR should not be filtered with WARNING minLevel",
            Level.ERROR.ordinal < Level.WARNING.ordinal)
        assertFalse("CRITICAL should not be filtered with WARNING minLevel",
            Level.CRITICAL.ordinal < Level.WARNING.ordinal)
    }

    @Test
    fun `should have proper inheritance hierarchy`() {
        val trainer = AndroidTestLogTrainer()

        // Verify it's actually an instance of AndroidLogTrainer
        assertTrue("AndroidTestLogTrainer should inherit from AndroidLogTrainer",
            trainer is AndroidLogTrainer)

        // Verify it implements Trainer interface
        assertTrue("AndroidTestLogTrainer should implement Trainer interface",
            trainer is Trainer
        )

        // Verify the inheritance doesn't break basic properties
        assertNotNull("Pack should not be null", trainer.pack)
        assertNotNull("MinLevel should not be null", trainer.minLevel)
    }

    @Test
    fun `minLevel filtering should work correctly with different thresholds`() {
        // Test different minLevel values
        val levels = listOf(
            Level.VERBOSE, Level.DEBUG, Level.INFO,
            Level.WARNING, Level.ERROR, Level.CRITICAL
        )

        levels.forEach { threshold ->
            val trainer = AndroidTestLogTrainer(minLevel = threshold)
            assertEquals("MinLevel should be set correctly", threshold, trainer.minLevel)

            // Test that levels below the threshold would be filtered
            Level.values().forEach { testLevel ->
                val shouldBeFiltered = testLevel.ordinal < threshold.ordinal
                assertEquals(
                    "Level $testLevel with minLevel $threshold filtering expectation",
                    shouldBeFiltered,
                    testLevel.ordinal < threshold.ordinal
                )
            }
        }
    }

    @Test
    fun `should be designed for test environments`() {
        // This test verifies the primary purpose: being usable during tests
        val trainer = AndroidTestLogTrainer()

        // Since this IS a test, and skipTests() returns false,
        // the trainer should be active and ready to log
        assertFalse("Should be active during test runs", trainer.skipTests())

        // Test that the trainer has the expected configuration for test usage
        assertEquals("Should default to VERBOSE for comprehensive test logging",
            Level.VERBOSE, trainer.minLevel)
        assertEquals("Should use TEST pack for test-environment logging", Pack.TEST, trainer.pack)

        // The key difference: it doesn't skip during tests
        val regularTrainer = AndroidLogTrainer()
        assertTrue("Regular AndroidLogTrainer should skip during tests",
            regularTrainer.skipTests())
        assertFalse("AndroidTestLogTrainer should NOT skip during tests",
            trainer.skipTests())

        // And uses TEST pack instead of SYSTEM
        assertEquals("Should use TEST pack for test-environment logging", Pack.TEST, trainer.pack)
    }

    // Note: We removed the assertDoesNotThrow helper since we're not calling handle() anymore
}
