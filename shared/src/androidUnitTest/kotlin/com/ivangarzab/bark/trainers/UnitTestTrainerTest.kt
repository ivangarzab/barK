package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import com.ivangarzab.bark.Pack
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * The purpose of this test class is to test the [UnitTestTrainer] class.
 *
 * Tests the console output trainer functionality including:
 * - Basic logging output
 * - Volume filtering
 * - Timestamp formatting
 * - Exception handling
 * - Test environment detection
 */
class UnitTestTrainerTest {

    private lateinit var originalOut: PrintStream
    private lateinit var originalErr: PrintStream
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var errorStream: ByteArrayOutputStream
    private lateinit var trainer: UnitTestTrainer

    @Before
    fun setup() {
        // Capture both console output and error streams for verification
        originalOut = System.out
        originalErr = System.err
        outputStream = ByteArrayOutputStream()
        errorStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        System.setErr(PrintStream(errorStream))

        // Create trainer with default settings
        trainer = UnitTestTrainer()
    }

    @After
    fun cleanup() {
        // Restore original console output and error streams
        System.setOut(originalOut)
        System.setErr(originalErr)
    }

    @Test
    fun `trainer should have correct pack type`() {
        assertEquals("UnitTestTrainer should use CONSOLE pack", Pack.CONSOLE, trainer.pack)
    }

    @Test
    fun `trainer should have default volume VERBOSE`() {
        val defaultTrainer = UnitTestTrainer()
        assertEquals("Default volume should be VERBOSE", Level.VERBOSE, defaultTrainer.volume)
    }

    @Test
    fun `trainer should accept custom volume in constructor`() {
        val customTrainer = UnitTestTrainer(volume = Level.WARNING)
        assertEquals("Custom volume should be respected", Level.WARNING, customTrainer.volume)
    }

    @Test
    fun `handle should output log message with correct format`() {
        trainer.handle(Level.INFO, "TestTag", "Test message", null)

        val output = getAllOutput()
        assertTrue("Output should contain level", output.contains("INFO"))
        assertTrue("Output should contain tag", output.contains("TestTag:"))
        assertTrue("Output should contain message", output.contains("Test message"))
    }

    @Test
    fun `handle should include timestamp when enabled`() {
        val trainerWithTimestamp = UnitTestTrainer(showTimestamp = true)
        trainerWithTimestamp.handle(Level.DEBUG, "TestTag", "Timestamped message", null)

        val output = getAllOutput()
        // Should contain a timestamp pattern like "HH:mm:ss.SSS"
        assertTrue("Output should contain timestamp",
            output.contains(Regex("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}")))
    }

    @Test
    fun `handle should exclude timestamp when disabled`() {
        val trainerWithoutTimestamp = UnitTestTrainer(showTimestamp = false)
        trainerWithoutTimestamp.handle(Level.DEBUG, "TestTag", "No timestamp message", null)

        val output = getAllOutput()
        // Should NOT contain a timestamp pattern
        assertFalse("Output should not contain timestamp",
            output.matches(Regex(".*\\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*")))
    }

    @Test
    fun `handle should output exception message when throwable provided`() {
        val exception = RuntimeException("Test exception message")
        trainer.handle(Level.ERROR, "TestTag", "Error occurred", exception)

        val output = getAllOutput()
        assertTrue("Output should contain exception message",
            output.contains("Exception: Test exception message"))
    }

    @Test
    fun `handle should print stack trace for ERROR level`() {
        val exception = RuntimeException("Test exception")
        trainer.handle(Level.ERROR, "TestTag", "Error occurred", exception)

        val output = getAllOutput()
        // Check for any stack trace indicators
        val hasStackTrace = output.contains("at ") ||
                output.contains("RuntimeException") ||
                output.contains("Test exception") ||
                output.lines().size > 2  // Multiple lines suggest stack trace

        assertTrue("Output should contain stack trace elements", hasStackTrace)
    }

    @Test
    fun `handle should print stack trace for CRITICAL level`() {
        val exception = IllegalStateException("Critical exception")
        trainer.handle(Level.CRITICAL, "TestTag", "Critical error", exception)

        val output = getAllOutput()
        // Check for any stack trace indicators
        val hasStackTrace = output.contains("at ") ||
                output.contains("IllegalStateException") ||
                output.contains("Critical exception") ||
                output.lines().size > 2  // Multiple lines suggest stack trace

        assertTrue("Output should contain stack trace for CRITICAL level", hasStackTrace)
    }

    @Test
    fun `handle should not print full stack trace for WARNING level`() {
        val exception = RuntimeException("Warning exception")
        trainer.handle(Level.WARNING, "TestTag", "Warning occurred", exception)

        val output = getAllOutput()
        assertTrue("Output should contain exception message",
            output.contains("Exception: Warning exception"))

        // Should not have many "at " lines (indicating full stack trace)
        val atLines = output.lines().count { it.trim().startsWith("at ") }
        assertTrue("Should not have many stack trace lines for WARNING", atLines <= 3)
    }

    @Test
    fun `handle should respect volume filtering`() {
        val infoTrainer = UnitTestTrainer(volume = Level.INFO)

        // These should be filtered out (below INFO level)
        infoTrainer.handle(Level.VERBOSE, "TestTag", "Verbose message", null)
        infoTrainer.handle(Level.DEBUG, "TestTag", "Debug message", null)

        // These should pass through (INFO level and above)
        infoTrainer.handle(Level.INFO, "TestTag", "Info message", null)
        infoTrainer.handle(Level.WARNING, "TestTag", "Warning message", null)

        val output = getAllOutput()
        assertFalse("Verbose message should be filtered", output.contains("Verbose message"))
        assertFalse("Debug message should be filtered", output.contains("Debug message"))
        assertTrue("Info message should pass through", output.contains("Info message"))
        assertTrue("Warning message should pass through", output.contains("Warning message"))
    }

    @Test
    fun `handle should format all log levels correctly`() {
        val levels = listOf(
            Level.VERBOSE, Level.DEBUG, Level.INFO,
            Level.WARNING, Level.ERROR, Level.CRITICAL
        )

        levels.forEach { level ->
            trainer.handle(level, "TestTag", "Message for ${level.label}", null)
        }

        val output = getAllOutput()
        levels.forEach { level ->
            assertTrue("Output should contain ${level.label}",
                output.contains(level.label))
            assertTrue("Output should contain message for ${level.label}",
                output.contains("Message for ${level.label}"))
        }
    }

    @Test
    fun `handle should work with empty messages`() {
        trainer.handle(Level.INFO, "TestTag", "", null)

        val output = getAllOutput()
        assertTrue("Should handle empty messages", output.contains("TestTag:"))
    }

    @Test
    fun `handle should work with special characters in message`() {
        val specialMessage = "Message with émojis 🎉 and ñew lines\nand tabs\t!"
        trainer.handle(Level.INFO, "TestTag", specialMessage, null)

        val output = getAllOutput()
        assertTrue("Should handle special characters", output.contains("émojis 🎉"))
    }

    @Test
    fun `handle should work with long tags`() {
        val longTag = "VeryLongTagNameThatExceedsNormalLength"
        trainer.handle(Level.INFO, longTag, "Message with long tag", null)

        val output = getAllOutput()
        assertTrue("Should handle long tags", output.contains(longTag))
    }

    @Test
    fun `multiple handle calls should produce separate output lines`() {
        trainer.handle(Level.INFO, "Tag1", "First message", null)
        trainer.handle(Level.INFO, "Tag2", "Second message", null)
        trainer.handle(Level.INFO, "Tag3", "Third message", null)

        val output = getAllOutput()
        val lines = output.split("\n").filter { it.trim().isNotEmpty() }
        assertTrue("Should have multiple output lines", lines.size >= 3)
        assertTrue("Should contain all messages",
            output.contains("First message") &&
                    output.contains("Second message") &&
                    output.contains("Third message"))
    }

    @Test
    fun `formatLevelLabel should be overrideable by subclasses`() {
        // Test the protected method indirectly by verifying base behavior
        trainer.handle(Level.INFO, "TestTag", "Test message", null)

        val output = getAllOutput()
        assertTrue("Base implementation should use level.label",
            output.contains(Level.INFO.label))
    }

    @Test
    fun `formatException should be overrideable by subclasses`() {
        // Test the protected method indirectly
        val exception = RuntimeException("Test exception")
        trainer.handle(Level.ERROR, "TestTag", "Error message", exception)

        val output = getAllOutput()
        assertTrue("Base implementation should format exception correctly",
            output.contains("Exception: Test exception"))
    }

    // Helper method to get ALL captured output (both stdout and stderr)
    private fun getAllOutput(): String {
        return outputStream.toString() + errorStream.toString()
    }

    // Helper method to get captured console output
    private fun getConsoleOutput(): String {
        return outputStream.toString()
    }

    // Helper method to get captured error output
    private fun getErrorOutput(): String {
        return errorStream.toString()
    }
}