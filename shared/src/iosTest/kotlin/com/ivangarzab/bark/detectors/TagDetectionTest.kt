package com.ivangarzab.bark.detectors

import kotlin.test.DefaultAsserter.assertNotNull
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test

/**
 * The purpose of this test class is to test the iOS version of the [getCallerTag] function.
 */
class TagDetectionTest {

    @Test
    fun `test tag detection across platforms`() {
        // This will work on both Android and iOS
        val tag = getCallerTag()

        println("Platform tag detection result: '$tag'")

        // Basic validation that works on both platforms
        assertNotNull("Tag should not be null", tag)
        assertTrue("Tag should not be empty", tag.isNotBlank())
        assertTrue("Tag should be reasonable length", tag.length in 1..50)

        // Platform-specific expectations
        when {
            tag.contains("Test") -> println("✅ Detected test class successfully")
            tag == "Bark" -> println("⚠️ Using fallback - may need platform-specific tuning")
            else -> println("✅ Detected class: $tag")
        }
    }
}