package com.ivangarzab.bark.detectors

import org.junit.Test
import org.junit.Assert.*

/**
 * Android unit tests for test detection functionality
 *
 * Tests the Android-specific detection of test environments,
 * which is crucial for trainers to behave differently during tests.
 *
 * Note: This test class uses JUnit instead of kotlin.test since we're
 * testing Android-specific functionality that relies on Java/Android APIs.
 */
class TestDetectionTest {

    @Test
    fun `isRunningTests should return true when called from unit test`() {
        // Since this IS a test, isRunningTests() should return true
        val result = isRunningTests()
        assertTrue("isRunningTests() should return true when called from a unit test", result)
    }

    @Test
    fun `isRunningTests should be consistent across multiple calls`() {
        // Should return the same result when called multiple times
        val result1 = isRunningTests()
        val result2 = isRunningTests()
        val result3 = isRunningTests()

        assertEquals("isRunningTests() should be consistent", result1, result2)
        assertEquals("isRunningTests() should be consistent", result2, result3)

        // And since we're in a test, all should be true
        assertTrue("First call should return true in test environment", result1)
        assertTrue("Second call should return true in test environment", result2)
        assertTrue("Third call should return true in test environment", result3)
    }

    @Test
    fun `isRunningTests should work when called from helper method`() {
        // Test that detection works even when called indirectly
        val result = helperMethodThatChecksIfTesting()
        assertTrue("isRunningTests() should work when called from helper methods", result)
    }

    @Test
    fun `isRunningTests should work when called from nested calls`() {
        // Test deeply nested calls
        val result = levelOne()
        assertTrue("isRunningTests() should work through multiple call levels", result)
    }

    @Test
    fun `isRunningTests should handle exceptions gracefully`() {
        // This test verifies that the function doesn't throw exceptions
        // Even if there are issues with stack trace inspection
        try {
            val result = isRunningTests()
            // Should return a boolean (true or false), not throw
            assertTrue("isRunningTests() should return a Boolean", result is Boolean)
        } catch (e: Exception) {
            fail("isRunningTests() should not throw exceptions, but got: ${e.message}")
        }
    }

    @Test
    fun `verify test environment characteristics`() {
        // Let's also verify some characteristics we expect in test environments
        val stackTrace = Thread.currentThread().stackTrace

        // Should find test-related classes in the stack
        val hasTestClasses = stackTrace.any { element ->
            val className = element.className.lowercase()
            className.contains("test") ||
                    className.contains("junit") ||
                    element.methodName.startsWith("test")
        }

        assertTrue("""
            Expected to find test-related classes in stack trace during test execution.
            
            Current stack trace classes:
            ${stackTrace.take(10).joinToString("\n") { "  ${it.className}.${it.methodName}" }}
            
            This helps verify that our test detection logic should work.
        """.trimIndent(), hasTestClasses)
    }

    // Helper methods to test indirect calls

    private fun helperMethodThatChecksIfTesting(): Boolean {
        return isRunningTests()
    }

    private fun levelOne(): Boolean {
        return levelTwo()
    }

    private fun levelTwo(): Boolean {
        return levelThree()
    }

    private fun levelThree(): Boolean {
        return isRunningTests()
    }
}