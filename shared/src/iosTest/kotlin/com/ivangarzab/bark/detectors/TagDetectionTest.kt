package com.ivangarzab.bark.detectors

import com.ivangarzab.bark.BarkConfig
import kotlin.test.*

/**
 * The purpose of this test class is to test the iOS version of the [getCallerTag] function.
 */
class TagDetectionTest {

    @BeforeTest
    fun setup() {
        // Enable auto-detection for tests
        BarkConfig.autoTagDisabled = false
    }

    @AfterTest
    fun teardown() {
        // Reset to default
        BarkConfig.autoTagDisabled = true
    }

    @Test
    fun `test tag detection with auto-detection enabled`() {
        // Enable auto-detection
        BarkConfig.autoTagDisabled = false

        val tag = getCallerTag()

        println("Platform tag detection result: '$tag'")

        // Basic validation
        assertNotNull(tag, "Tag should not be null")
        assertTrue(tag.isNotBlank(), "Tag should not be empty when auto-detection is enabled")
        assertTrue(tag.length in 1..50, "Tag should be reasonable length")

        // Should detect test class name
        assertTrue(
            tag.contains("TagDetectionTest") || tag.contains("Test"),
            "Should detect test class name, got: '$tag'"
        )
        println("✅ Detected test class successfully: '$tag'")
    }

    @Test
    fun `test tag detection with auto-detection disabled`() {
        // Disable auto-detection
        BarkConfig.autoTagDisabled = true

        val tag = getCallerTag()

        println("Tag with auto-detection disabled: '$tag'")

        // Should return empty string when disabled
        assertEquals("", tag, "Should return empty string when auto-detection is disabled")
        println("✅ Correctly returns empty string when disabled")
    }

    @Test
    fun `test tag detection toggle behavior`() {
        // Test that we can toggle on and off
        BarkConfig.autoTagDisabled = true
        val disabledTag = getCallerTag()
        assertEquals("", disabledTag, "Should be empty when disabled")

        BarkConfig.autoTagDisabled = false
        val enabledTag = getCallerTag()
        assertTrue(enabledTag.isNotBlank(), "Should detect tag when enabled")

        println("✅ Toggle behavior works correctly")
    }
}