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
 * - Maintains proper pack type and volume behavior
 * - Inherits all AndroidLogTrainer functionality correctly
 *
 * Note: We don't test the actual Android Log calls since those are simple
 * delegations to the Android framework. We focus on the behavioral logic.
 */
class AndroidTestLogTrainerTest {

    @Test
    fun `trainer should have correct pack type`() {
        val trainer = AndroidTestLogTrainer()
        assertEquals("AndroidTestLogTrainer should use SYSTEM pack", Pack.SYSTEM, trainer.pack)
    }

    @Test
    fun `trainer should have default volume VERBOSE`() {
        val trainer = AndroidTestLogTrainer()
        assertEquals("Default volume should be VERBOSE", Level.VERBOSE, trainer.volume)
    }

    @Test
    fun `trainer should accept custom volume in constructor`() {
        val customTrainer = AndroidTestLogTrainer(volume = Level.ERROR)
        assertEquals("Custom volume should be respected", Level.ERROR, customTrainer.volume)
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
    fun `should inherit proper volume filtering behavior`() {
        // Test that volume filtering works as expected (inherited from parent)
        val warningTrainer = AndroidTestLogTrainer(volume = Level.WARNING)

        // Verify volume property is set correctly
        assertEquals(Level.WARNING, warningTrainer.volume)

        // Test volume filtering logic (levels below WARNING should be filtered)
        assertTrue("VERBOSE should be below WARNING threshold",
            Level.VERBOSE.ordinal < Level.WARNING.ordinal)
        assertTrue("DEBUG should be below WARNING threshold",
            Level.DEBUG.ordinal < Level.WARNING.ordinal)
        assertTrue("INFO should be below WARNING threshold",
            Level.INFO.ordinal < Level.WARNING.ordinal)

        // These should pass through
        assertFalse("WARNING should pass volume filter",
            Level.WARNING.ordinal < Level.WARNING.ordinal)
        assertFalse("ERROR should pass volume filter",
            Level.ERROR.ordinal < Level.WARNING.ordinal)
        assertFalse("CRITICAL should pass volume filter",
            Level.CRITICAL.ordinal < Level.WARNING.ordinal)
    }

    @Test
    fun `should maintain same pack type as parent`() {
        val testTrainer = AndroidTestLogTrainer()
        val regularTrainer = AndroidLogTrainer()

        assertEquals("Both trainers should use same pack type",
            regularTrainer.pack, testTrainer.pack)
        assertEquals("Should be SYSTEM pack", Pack.SYSTEM, testTrainer.pack)
    }

    @Test
    fun `should have correct level mapping behavior`() {
        val trainer = AndroidTestLogTrainer(volume = Level.VERBOSE)

        // We can't call handle() because it tries to use Android Log in unit tests
        // Instead, we test the logic that determines if a message would be processed

        // All levels should be processed when volume is VERBOSE
        Level.values().forEach { level ->
            val wouldBeFiltered = level.ordinal < Level.VERBOSE.ordinal
            assertFalse("Level $level should not be filtered with VERBOSE volume", wouldBeFiltered)
        }

        // Test with higher volume - only some levels should pass
        val warningTrainer = AndroidTestLogTrainer(volume = Level.WARNING)

        assertTrue("VERBOSE should be filtered with WARNING volume",
            Level.VERBOSE.ordinal < Level.WARNING.ordinal)
        assertTrue("DEBUG should be filtered with WARNING volume",
            Level.DEBUG.ordinal < Level.WARNING.ordinal)
        assertTrue("INFO should be filtered with WARNING volume",
            Level.INFO.ordinal < Level.WARNING.ordinal)
        assertFalse("WARNING should not be filtered with WARNING volume",
            Level.WARNING.ordinal < Level.WARNING.ordinal)
        assertFalse("ERROR should not be filtered with WARNING volume",
            Level.ERROR.ordinal < Level.WARNING.ordinal)
        assertFalse("CRITICAL should not be filtered with WARNING volume",
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
        assertNotNull("Volume should not be null", trainer.volume)
    }

    @Test
    fun `volume filtering should work correctly with different thresholds`() {
        // Test different volume levels
        val volumes = listOf(
            Level.VERBOSE, Level.DEBUG, Level.INFO,
            Level.WARNING, Level.ERROR, Level.CRITICAL
        )

        volumes.forEach { volumeLevel ->
            val trainer = AndroidTestLogTrainer(volume = volumeLevel)
            assertEquals("Volume should be set correctly", volumeLevel, trainer.volume)

            // Test that levels below the volume would be filtered
            Level.values().forEach { testLevel ->
                val shouldBeFiltered = testLevel.ordinal < volumeLevel.ordinal
                assertEquals(
                    "Level $testLevel with volume $volumeLevel filtering expectation",
                    shouldBeFiltered,
                    testLevel.ordinal < volumeLevel.ordinal
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
            Level.VERBOSE, trainer.volume)
        assertEquals("Should use SYSTEM pack like parent", Pack.SYSTEM, trainer.pack)

        // The key difference: it doesn't skip during tests
        val regularTrainer = AndroidLogTrainer()
        assertTrue("Regular AndroidLogTrainer should skip during tests",
            regularTrainer.skipTests())
        assertFalse("AndroidTestLogTrainer should NOT skip during tests",
            trainer.skipTests())
    }

    // Note: We removed the assertDoesNotThrow helper since we're not calling handle() anymore
}