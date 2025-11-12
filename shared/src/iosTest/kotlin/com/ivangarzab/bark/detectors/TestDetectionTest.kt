package com.ivangarzab.bark.detectors

import kotlin.test.*
import platform.Foundation.NSThread

/**
 * iOS unit tests for test detection functionality
 *
 * Tests the iOS-specific detection of test environments,
 * which is crucial for trainers to behave differently during tests.
 *
 * Note: This test class uses kotlin.test since we're testing
 * iOS-specific functionality that relies on Foundation/XCTest APIs.
 */
class TestDetectionTest {

    @Test
    fun `isRunningTests should return true when called from unit test`() {
        // Since this IS a test, isRunningTests() should return true
        val result = isRunningTests()
        assertTrue(result, "isRunningTests() should return true when called from a unit test")
    }

    @Test
    fun `isRunningTests should be consistent across multiple calls`() {
        // Should return the same result when called multiple times
        val result1 = isRunningTests()
        val result2 = isRunningTests()
        val result3 = isRunningTests()

        assertEquals(result1, result2, "isRunningTests() should be consistent")
        assertEquals(result2, result3, "isRunningTests() should be consistent")

        // And since we're in a test, all should be true
        assertTrue(result1, "First call should return true in test environment")
        assertTrue(result2, "Second call should return true in test environment")
        assertTrue(result3, "Third call should return true in test environment")
    }

    @Test
    fun `isRunningTests should work when called from helper method`() {
        // Test that detection works even when called indirectly
        val result = helperMethodThatChecksIfTesting()
        assertTrue(result, "isRunningTests() should work when called from helper methods")
    }

    @Test
    fun `isRunningTests should work when called from nested calls`() {
        // Test deeply nested calls
        val result = levelOne()
        assertTrue(result, "isRunningTests() should work through multiple call levels")
    }

    @Test
    fun `isRunningTests should handle exceptions gracefully`() {
        // This test verifies that the function doesn't throw exceptions
        // Even if there are issues with stack trace inspection
        try {
            val result = isRunningTests()
            // Should return a boolean (true or false), not throw
            assertTrue(result is Boolean, "isRunningTests() should return a Boolean")
        } catch (e: Exception) {
            fail("isRunningTests() should not throw exceptions, but got: ${e.message}")
        }
    }

    @Test
    fun `verify test environment characteristics`() {
        // Let's also verify some characteristics we expect in test environments
        val callStackSymbols = NSThread.callStackSymbols

        // Should find test-related strings in the stack
        val hasTestSymbols = callStackSymbols.any { symbol ->
            val symbolString = symbol.toString().lowercase()
            symbolString.contains("xctest") ||
                    symbolString.contains("test")
        }

        assertTrue(
            hasTestSymbols, """
            Expected to find test-related symbols in call stack during test execution.

            Current call stack symbols (first 10):
            ${callStackSymbols.take(10).joinToString("\n") { "  $it" }}

            This helps verify that our test detection logic should work.
        """.trimIndent()
        )
    }

    @Test
    fun `isRunningTests should detect XCTest framework`() {
        // Verify that we can detect the XCTest framework in the call stack
        val result = isRunningTests()
        assertTrue(result, "Should detect XCTest framework during test execution")
    }

    @Test
    fun `call stack should contain XCTest-related symbols`() {
        // Verify the call stack inspection approach
        val callStackSymbols = NSThread.callStackSymbols

        val hasXCTestSymbol = callStackSymbols.any { symbol ->
            symbol.toString().lowercase().contains("xctest")
        }

        assertTrue(
            hasXCTestSymbol,
            "Call stack should contain XCTest-related symbols during test execution"
        )
    }

    @Test
    fun `isRunningTests should use lowercase comparison`() {
        // Verify that the implementation properly handles case-insensitive checks
        // This is important for reliable detection across different iOS versions
        val result = isRunningTests()

        // We're in a test, so this should be true
        assertTrue(result, "Should use case-insensitive comparison for test detection")
    }

    @Test
    fun `isRunningTests implementation should be resilient to stack variations`() {
        // Test that detection works even with complex call patterns
        val result = complexCallPattern()
        assertTrue(result, "Should detect tests even with complex call patterns")
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

    private fun complexCallPattern(): Boolean {
        return nestedHelper {
            deeperNesting {
                isRunningTests()
            }
        }
    }

    private inline fun nestedHelper(block: () -> Boolean): Boolean {
        return block()
    }

    private inline fun deeperNesting(block: () -> Boolean): Boolean {
        return block()
    }
}