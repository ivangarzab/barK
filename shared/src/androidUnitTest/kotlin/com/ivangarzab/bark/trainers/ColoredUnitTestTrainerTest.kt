package com.ivangarzab.bark.trainers

import com.ivangarzab.bark.Level
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * The purpose of this test class is to test the [ColoredUnitTestTrainerTest] class.
 *
 * Focuses on testing the color functionality that extends UnitTestTrainer.
 * Base functionality (volume filtering, timestamps, etc.) is tested in UnitTestTrainerTest.
 */
class ColoredUnitTestTrainerTest {

    private lateinit var originalOut: PrintStream
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var trainer: ColoredUnitTestTrainer

    @Before
    fun setup() {
        // Capture console output for verification
        originalOut = System.out
        outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        // Create trainer with default settings
        trainer = ColoredUnitTestTrainer()
    }

    @After
    fun cleanup() {
        // Restore original console output
        System.setOut(originalOut)
    }

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

    @Test
    fun `should use gray color for VERBOSE level`() {
        trainer.handle(Level.VERBOSE, "TestTag", "Verbose message", null)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_GRAY, "[VERBOSE]", ANSI_RESET)
    }

    @Test
    fun `should use blue color for DEBUG level`() {
        trainer.handle(Level.DEBUG, "TestTag", "Debug message", null)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_BLUE, "[DEBUG]", ANSI_RESET)
    }

    @Test
    fun `should use green color for INFO level`() {
        trainer.handle(Level.INFO, "TestTag", "Info message", null)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_GREEN, "[INFO]", ANSI_RESET)
    }

    @Test
    fun `should use yellow color for WARNING level`() {
        trainer.handle(Level.WARNING, "TestTag", "Warning message", null)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_YELLOW, "[WARNING]", ANSI_RESET)
    }

    @Test
    fun `should use red color for ERROR level`() {
        trainer.handle(Level.ERROR, "TestTag", "Error message", null)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_RED, "[ERROR]", ANSI_RESET)
    }

    @Test
    fun `should use bright red color for CRITICAL level`() {
        trainer.handle(Level.CRITICAL, "TestTag", "Critical message", null)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_BRIGHT_RED, "[CRITICAL]", ANSI_RESET)
    }

    @Test
    fun `should use red color for exception messages`() {
        val exception = RuntimeException("Test exception")
        trainer.handle(Level.ERROR, "TestTag", "Error occurred", exception)

        val output = getConsoleOutput()
        assertContainsColorSequence(output, ANSI_RED, "Exception: Test exception", ANSI_RESET)
    }

    @Test
    fun `should have unique colors for each log level`() {
        val levelColorMap = mapOf(
            Level.VERBOSE to ANSI_GRAY,
            Level.DEBUG to ANSI_BLUE,
            Level.INFO to ANSI_GREEN,
            Level.WARNING to ANSI_YELLOW,
            Level.ERROR to ANSI_RED,
            Level.CRITICAL to ANSI_BRIGHT_RED
        )

        levelColorMap.forEach { (level, expectedColor) ->
            outputStream.reset()
            trainer.handle(level, "TestTag", "Message", null)

            val output = getConsoleOutput()
            assertTrue("$level should use color $expectedColor",
                output.contains(expectedColor))
        }
    }

    @Test
    fun `should properly reset colors after level labels`() {
        trainer.handle(Level.INFO, "TestTag", "Test message", null)

        val output = getConsoleOutput()
        // Should have the pattern: COLOR + [LEVEL] + RESET
        assertTrue("Should reset color after level label",
            output.contains("$ANSI_GREEN[INFO]$ANSI_RESET"))
    }

    @Test
    fun `should properly reset colors after exception messages`() {
        val exception = RuntimeException("Test exception")
        trainer.handle(Level.ERROR, "TestTag", "Error", exception)

        val output = getConsoleOutput()
        // Should have: COLOR + Exception: message + RESET
        val pattern = "${ANSI_RED}Exception: Test exception$ANSI_RESET"
        assertTrue("Should reset color after exception message",
            output.contains(pattern))
    }

    @Test
    fun `colored output should maintain same structure as parent class`() {
        val plainTrainer = UnitTestTrainer(showTimestamp = false)
        val coloredTrainer = ColoredUnitTestTrainer(showTimestamp = false)

        // Get output from both trainers
        plainTrainer.handle(Level.INFO, "TestTag", "Test message", null)
        val plainOutput = outputStream.toString()

        outputStream.reset()
        coloredTrainer.handle(Level.INFO, "TestTag", "Test message", null)
        val coloredOutput = outputStream.toString()

        // Remove ANSI codes from colored output
        val coloredWithoutAnsi = removeAnsiCodes(coloredOutput)

        assertEquals("Structure should be identical when ANSI codes removed",
            plainOutput.trim(), coloredWithoutAnsi.trim())
    }

    @Test
    fun `should preserve timestamp functionality when enabled`() {
        val timestampTrainer = ColoredUnitTestTrainer(showTimestamp = true)
        timestampTrainer.handle(Level.DEBUG, "TestTag", "Timestamped message", null)

        val output = getConsoleOutput()
        val timestampPattern = Regex("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}")
        assertTrue("Should include timestamp",
            timestampPattern.containsMatchIn(output))
        assertTrue("Should still have colors",
            output.contains(ANSI_BLUE))
    }

    @Test
    fun `should preserve volume filtering functionality`() {
        val warningTrainer = ColoredUnitTestTrainer(volume = Level.WARNING)

        warningTrainer.handle(Level.DEBUG, "TestTag", "Debug message", null)
        warningTrainer.handle(Level.WARNING, "TestTag", "Warning message", null)

        val output = getConsoleOutput()

        assertFalse("Debug should be filtered out", output.contains("Debug message"))
        assertTrue("Warning should pass through", output.contains("Warning message"))
        assertTrue("Warning should be colored", output.contains(ANSI_YELLOW))
    }

    // Helper method to assert a color sequence exists in output
    private fun assertContainsColorSequence(output: String, colorCode: String, content: String, resetCode: String) {
        assertTrue("Output should contain color code: $colorCode",
            output.contains(colorCode))
        assertTrue("Output should contain content: $content",
            output.contains(content))
        assertTrue("Output should contain reset code: $resetCode",
            output.contains(resetCode))
    }

    // Helper method to remove ANSI codes for comparison
    private fun removeAnsiCodes(text: String): String {
        return text.replace(Regex("\u001B\\[[0-9;]*m"), "")
    }

    // Helper method to get captured console output
    private fun getConsoleOutput(): String {
        return outputStream.toString()
    }
}