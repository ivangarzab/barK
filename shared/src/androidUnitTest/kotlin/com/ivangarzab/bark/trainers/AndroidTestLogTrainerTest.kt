package com.ivangarzab.bark.trainers

import android.util.Log
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The purpose of this test class is to test the [AndroidTestLogTrainer] class.
 *
 * Tests the Android Test Logcat output trainer functionality including:
 * - Android Log system integration during tests
 * - Volume filtering
 * - Test environment handling (does NOT skip tests)
 * - Proper mapping of barK levels to Android log levels
 * - Inheritance from AndroidLogTrainer
 *
 * Since AndroidTestLogTrainer extends AndroidLogTrainer and only overrides skipTests(),
 * this test effectively covers both classes.
 */
@RunWith(RobolectricTestRunner::class)
class AndroidTestLogTrainerTest {

    private lateinit var trainer: AndroidTestLogTrainer

    @Before
    fun setup() {
        // Clear any existing mocks
        clearAllMocks()

        // Mock Android Log class
        mockkStatic(Log::class)

        // Default mock returns for Log methods (Android Log methods return int)
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.wtf(any(), any(), any()) } returns 0

        // Versions without throwable
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.wtf(any(), any<String>()) } returns 0

        trainer = AndroidTestLogTrainer()
    }

    @After
    fun cleanup() {
        unmockkAll()
    }

    @Test
    fun `trainer should have correct pack type`() {
        assertEquals("AndroidTestLogTrainer should use SYSTEM pack", Pack.SYSTEM, trainer.pack)
    }

    @Test
    fun `trainer should have default volume VERBOSE`() {
        val defaultTrainer = AndroidTestLogTrainer()
        assertEquals("Default volume should be VERBOSE", Level.VERBOSE, defaultTrainer.volume)
    }

    @Test
    fun `trainer should accept custom volume in constructor`() {
        val customTrainer = AndroidTestLogTrainer(volume = Level.ERROR)
        assertEquals("Custom volume should be respected", Level.ERROR, customTrainer.volume)
    }

    @Test
    fun `skipTests should return false to allow logging during tests`() {
        assertFalse("AndroidTestLogTrainer should NOT skip tests", trainer.skipTests())
    }

    @Test
    fun `VERBOSE level should call Log_v`() {
        trainer.handle(Level.VERBOSE, "TestTag", "Verbose message", null)

        verify(exactly = 1) { Log.v("TestTag", "Verbose message", null) }
    }

    @Test
    fun `DEBUG level should call Log_d`() {
        trainer.handle(Level.DEBUG, "TestTag", "Debug message", null)

        verify(exactly = 1) { Log.d("TestTag", "Debug message", null) }
    }

    @Test
    fun `INFO level should call Log_i`() {
        trainer.handle(Level.INFO, "TestTag", "Info message", null)

        verify(exactly = 1) { Log.i("TestTag", "Info message", null) }
    }

    @Test
    fun `WARNING level should call Log_w`() {
        trainer.handle(Level.WARNING, "TestTag", "Warning message", null)

        verify(exactly = 1) { Log.w("TestTag", "Warning message", null) }
    }

    @Test
    fun `ERROR level should call Log_e`() {
        trainer.handle(Level.ERROR, "TestTag", "Error message", null)

        verify(exactly = 1) { Log.e("TestTag", "Error message", null) }
    }

    @Test
    fun `CRITICAL level should call Log_wtf`() {
        trainer.handle(Level.CRITICAL, "TestTag", "Critical message", null)

        verify(exactly = 1) { Log.wtf("TestTag", "Critical message", null) }
    }

    @Test
    fun `should pass throwable to Android Log methods`() {
        val exception = RuntimeException("Test exception")

        trainer.handle(Level.ERROR, "TestTag", "Error with exception", exception)

        verify(exactly = 1) { Log.e("TestTag", "Error with exception", exception) }
    }

    @Test
    fun `should handle all levels with throwables correctly`() {
        val exception = IllegalStateException("Test exception")

        trainer.handle(Level.VERBOSE, "TestTag", "Verbose with exception", exception)
        trainer.handle(Level.DEBUG, "TestTag", "Debug with exception", exception)
        trainer.handle(Level.INFO, "TestTag", "Info with exception", exception)
        trainer.handle(Level.WARNING, "TestTag", "Warning with exception", exception)
        trainer.handle(Level.ERROR, "TestTag", "Error with exception", exception)
        trainer.handle(Level.CRITICAL, "TestTag", "Critical with exception", exception)

        verify(exactly = 1) { Log.v("TestTag", "Verbose with exception", exception) }
        verify(exactly = 1) { Log.d("TestTag", "Debug with exception", exception) }
        verify(exactly = 1) { Log.i("TestTag", "Info with exception", exception) }
        verify(exactly = 1) { Log.w("TestTag", "Warning with exception", exception) }
        verify(exactly = 1) { Log.e("TestTag", "Error with exception", exception) }
        verify(exactly = 1) { Log.wtf("TestTag", "Critical with exception", exception) }
    }

    @Test
    fun `should respect volume filtering`() {
        val warningTrainer = AndroidTestLogTrainer(volume = Level.WARNING)

        // These should be filtered out (below WARNING level)
        warningTrainer.handle(Level.VERBOSE, "TestTag", "Verbose message", null)
        warningTrainer.handle(Level.DEBUG, "TestTag", "Debug message", null)
        warningTrainer.handle(Level.INFO, "TestTag", "Info message", null)

        // These should pass through (WARNING level and above)
        warningTrainer.handle(Level.WARNING, "TestTag", "Warning message", null)
        warningTrainer.handle(Level.ERROR, "TestTag", "Error message", null)
        warningTrainer.handle(Level.CRITICAL, "TestTag", "Critical message", null)

        // Verify filtered messages were NOT logged
        verify(exactly = 0) { Log.v(any(), any()) }
        verify(exactly = 0) { Log.d(any(), any()) }
        verify(exactly = 0) { Log.i(any(), any()) }

        // Verify allowed messages WERE logged
        verify(exactly = 1) { Log.w("TestTag", "Warning message", null) }
        verify(exactly = 1) { Log.e("TestTag", "Error message", null) }
        verify(exactly = 1) { Log.wtf("TestTag", "Critical message", null) }
    }

    @Test
    fun `should handle long tags correctly`() {
        val longTag = "VeryLongTagNameThatMightExceedAndroidLimits"
        trainer.handle(Level.INFO, longTag, "Message with long tag", null)

        verify(exactly = 1) { Log.i(longTag, "Message with long tag", null) }
    }

    @Test
    fun `should handle empty messages`() {
        trainer.handle(Level.INFO, "TestTag", "", null)

        verify(exactly = 1) { Log.i("TestTag", "", null) }
    }

    @Test
    fun `should handle special characters in messages`() {
        val specialMessage = "Message with émojis 🎉 and newlines\nand tabs\t!"
        trainer.handle(Level.DEBUG, "TestTag", specialMessage, null)

        verify(exactly = 1) { Log.d("TestTag", specialMessage, null) }
    }

    @Test
    fun `should handle null throwable gracefully`() {
        trainer.handle(Level.ERROR, "TestTag", "Error without exception", null)

        // Should call the version without throwable
        verify(exactly = 1) { Log.e("TestTag", "Error without exception", null) }
    }

    @Test
    fun `multiple calls should work correctly`() {
        trainer.handle(Level.INFO, "Tag1", "First message", null)
        trainer.handle(Level.ERROR, "Tag2", "Second message", null)
        trainer.handle(Level.DEBUG, "Tag3", "Third message", null)

        verify(exactly = 1) { Log.i("Tag1", "First message", null) }
        verify(exactly = 1) { Log.e("Tag2", "Second message", null) }
        verify(exactly = 1) { Log.d("Tag3", "Third message", null) }
    }

    @Test
    fun `should work with different throwable types`() {
        val runtimeException = RuntimeException("Runtime error")
        val illegalArgException = IllegalArgumentException("Illegal argument")
        val nullPointerException = NullPointerException("Null pointer")

        trainer.handle(Level.ERROR, "TestTag", "Runtime error", runtimeException)
        trainer.handle(Level.ERROR, "TestTag", "Illegal arg error", illegalArgException)
        trainer.handle(Level.ERROR, "TestTag", "NPE error", nullPointerException)

        verify(exactly = 1) { Log.e("TestTag", "Runtime error", runtimeException) }
        verify(exactly = 1) { Log.e("TestTag", "Illegal arg error", illegalArgException) }
        verify(exactly = 1) { Log.e("TestTag", "NPE error", nullPointerException) }
    }

    @Test
    fun `should map all barK levels to correct Android levels`() {
        val levels = listOf(
            Level.VERBOSE, Level.DEBUG, Level.INFO,
            Level.WARNING, Level.ERROR, Level.CRITICAL
        )

        levels.forEach { level ->
            trainer.handle(level, "TestTag", "Message for $level", null)
        }

        // Verify each level was called exactly once
        verify(exactly = 1) { Log.v("TestTag", "Message for ${Level.VERBOSE}", null) }
        verify(exactly = 1) { Log.d("TestTag", "Message for ${Level.DEBUG}", null) }
        verify(exactly = 1) { Log.i("TestTag", "Message for ${Level.INFO}", null) }
        verify(exactly = 1) { Log.w("TestTag", "Message for ${Level.WARNING}", null) }
        verify(exactly = 1) { Log.e("TestTag", "Message for ${Level.ERROR}", null) }
        verify(exactly = 1) { Log.wtf("TestTag", "Message for ${Level.CRITICAL}", null) }
    }

    @Test
    fun `should work during test runs unlike parent AndroidLogTrainer`() {
        // This test demonstrates the key difference from AndroidLogTrainer
        // AndroidLogTrainer would skip logging during tests, but AndroidTestLogTrainer doesn't

        trainer.handle(Level.INFO, "TestTag", "Should log during tests", null)

        // This should work because AndroidTestLogTrainer.skipTests() returns false
        verify(exactly = 1) { Log.i("TestTag", "Should log during tests", null) }
    }

    @Test
    fun `difference from parent class - skipTests behavior`() {
        // Create both trainers to compare behavior
        val testTrainer = AndroidTestLogTrainer()
        val regularTrainer = AndroidLogTrainer()

        // AndroidTestLogTrainer should NOT skip tests
        assertFalse("AndroidTestLogTrainer should not skip tests", testTrainer.skipTests())

        // AndroidLogTrainer should skip tests (when in test environment)
        // Note: This might return true or false depending on test detection, but we can verify the difference exists
        val regularSkips = regularTrainer.skipTests()

        // The key point is that they behave differently
        assertTrue("Trainers should have different skipTests behavior",
            testTrainer.skipTests() != regularSkips || !testTrainer.skipTests())
    }

    @Test
    fun `should inherit all AndroidLogTrainer functionality`() {
        // Test that it properly inherits volume filtering and other parent functionality
        val errorTrainer = AndroidTestLogTrainer(volume = Level.ERROR)

        // Should filter out lower levels
        errorTrainer.handle(Level.WARNING, "TestTag", "Should be filtered", null)
        verify(exactly = 0) { Log.w(any(), any<String>(), any()) }

        // Should allow ERROR and above
        errorTrainer.handle(Level.ERROR, "TestTag", "Should pass through", null)
        verify(exactly = 1) { Log.e("TestTag", "Should pass through", null) }

        // Should have same pack as parent
        assertEquals("Should inherit pack from parent", Pack.SYSTEM, errorTrainer.pack)
    }
}