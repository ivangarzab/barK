package com.ivangarzab.bark

import org.junit.Test
import org.junit.Assert.*

/**
 * Android unit tests for automatic tag detection functionality
 *
 * Tests the stack trace inspection logic that automatically determines
 * the calling class name for use as a log tag, similar to Timber's behavior.
 */
class TagDetectionTest {

    @Test
    fun `getCallerTag should return calling class name`() {
        // When called directly from this test class, should return the test class name
        val tag = getCallerTag()

        assertTrue(
            "Expected tag to contain 'TagDetectionTest' but got: $tag",
            tag.contains("TagDetectionTest")
        )
    }

    @Test
    fun `getCallerTag should work when called from helper method`() {
        // Test that it correctly identifies the calling class even through helper methods
        val tag = helperMethodThatGetsTag()

        assertTrue(
            "Expected tag to contain 'TagDetectionTest' when called through helper method, but got: $tag",
            tag.contains("TagDetectionTest")
        )
    }

    @Test
    fun `getCallerTag should work through nested calls`() {
        // Test deeply nested calls
        val tag = levelOne()

        assertTrue(
            "Expected tag to contain 'TagDetectionTest' through nested calls, but got: $tag",
            tag.contains("TagDetectionTest")
        )
    }

    @Test
    fun `getCallerTag should handle inner class scenarios`() {
        // Test from an inner class
        val innerClassCaller = InnerClassCaller()
        val tag = innerClassCaller.getTagFromInnerClass()

        // Should get the outer class name, not the inner class
        assertTrue(
            "Expected tag to contain 'TagDetectionTest' from inner class, but got: $tag",
            tag.contains("TagDetectionTest")
        )
    }

    @Test
    fun `getCallerTag should respect Android tag length limit`() {
        // Tag should not exceed Android's 23-character limit
        val tag = getCallerTag()

        assertTrue(
            "Android log tags must be 23 characters or less, but got ${tag.length}: '$tag'",
            tag.length <= 23
        )
    }

    @Test
    fun `getCallerTag should not return bark framework classes`() {
        // Should skip any barK-related classes in the stack
        val tag = getCallerTag()

        // Should not contain barK framework class names
        assertFalse("Tag should not end with 'Bark': $tag", tag.endsWith("Bark"))
        assertFalse("Tag should not contain 'Trainer': $tag", tag.contains("Trainer"))
    }

    @Test
    fun `getCallerTag should not return system classes`() {
        // Should skip system classes
        val tag = getCallerTag()

        // Should not start with common system class prefixes
        assertFalse("Tag should not start with 'java.': $tag", tag.startsWith("java."))
        assertFalse("Tag should not start with 'android.': $tag", tag.startsWith("android."))
        assertFalse("Tag should not start with 'kotlin.': $tag", tag.startsWith("kotlin."))
        assertFalse("Tag should not start with 'dalvik.': $tag", tag.startsWith("dalvik."))
    }

    @Test
    fun `getCallerTag should handle exceptions gracefully`() {
        // Should not throw exceptions even if stack trace inspection fails
        try {
            val tag = getCallerTag()
            assertNotNull("getCallerTag() should not return null", tag)
            assertTrue("getCallerTag() should return non-empty string", tag.isNotBlank())
        } catch (e: Exception) {
            fail("getCallerTag() should not throw exceptions, but got: ${e.message}")
        }
    }

    @Test
    fun `getCallerTag should return consistent results`() {
        // Should return the same tag when called multiple times from the same location
        val tag1 = getCallerTag()
        val tag2 = getCallerTag()
        val tag3 = getCallerTag()

        assertEquals("getCallerTag() should be consistent", tag1, tag2)
        assertEquals("getCallerTag() should be consistent", tag2, tag3)
    }

    @Test
    fun `getCallerTag should handle different calling contexts`() {
        // Test from different method contexts
        val tagFromTest = getCallerTag()
        val tagFromHelper = getTagFromHelper()
        val tagFromLambda = run { getCallerTag() }

        // All should identify this test class as the real caller
        assertTrue("Tag from test method should contain test class", tagFromTest.contains("TagDetectionTest"))
        assertTrue("Tag from helper should contain test class", tagFromHelper.contains("TagDetectionTest"))
        assertTrue("Tag from lambda should contain test class", tagFromLambda.contains("TagDetectionTest"))
    }

    // Helper methods for testing

    private fun helperMethodThatGetsTag(): String {
        return getCallerTag()
    }

    private fun levelOne(): String {
        return levelTwo()
    }

    private fun levelTwo(): String {
        return levelThree()
    }

    private fun levelThree(): String {
        return getCallerTag()
    }

    private fun getTagFromHelper(): String {
        return getCallerTag()
    }

    // Inner class for testing inner class scenarios
    inner class InnerClassCaller {
        fun getTagFromInnerClass(): String {
            return getCallerTag()
        }
    }

    // Companion object for testing static-like calls
    companion object {
        fun getTagFromCompanion(): String {
            return getCallerTag()
        }
    }
}

/**
 * Separate test class to verify cross-class tag detection
 */
class AnotherTestClass {

    @Test
    fun `getCallerTag should identify different calling class`() {
        val tag = getCallerTag()

        assertTrue(
            "Expected tag to contain 'AnotherTestClass' but got: $tag",
            tag.contains("AnotherTestClass")
        )

        // Should NOT contain the previous test class name
        assertFalse(
            "Tag should not contain 'TagDetectionTest' when called from different class",
            tag.contains("TagDetectionTest")
        )
    }
}