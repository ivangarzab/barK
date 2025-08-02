package com.ivangarzab.bark

import kotlin.test.*

/**
 * Unit tests for the main Bark API object
 *
 * Tests core functionality including:
 * - Basic logging methods
 * - Trainer management
 * - Muzzle/unmuzzle behavior
 * - Tag configuration
 * - Status reporting
 */
class BarkTest {

    // Test trainer that captures log calls for verification
    private class TestTrainer : Trainer {
        val logCalls = mutableListOf<LogCall>()

        override fun handle(level: Level, tag: String, message: String, throwable: Throwable?) {
            logCalls.add(LogCall(level, tag, message, throwable))
        }

        fun clear() = logCalls.clear()
        fun getLastCall() = logCalls.lastOrNull()
        fun getCallCount() = logCalls.size
    }

    // Data class to capture log call details
    data class LogCall(
        val level: Level,
        val tag: String,
        val message: String,
        val throwable: Throwable?
    )

    private lateinit var testTrainer: TestTrainer

    @BeforeTest
    fun setup() {
        // Clean slate for each test
        Bark.releaseAllTrainers()
        Bark.unmuzzle()

        // Reset global tag
        Bark.tag("TestTag") // Set a predictable tag for testing

        // Setup test trainer
        testTrainer = TestTrainer()
        Bark.train(testTrainer)
    }

    @AfterTest
    fun cleanup() {
        Bark.releaseAllTrainers()
        Bark.unmuzzle()
    }

    @Test
    fun `verbose logging should call trainer with VERBOSE level`() {
        Bark.v("Test verbose message")

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals(Level.VERBOSE, call.level)
        assertEquals("Test verbose message", call.message)
        assertEquals("TestTag", call.tag)
        assertNull(call.throwable)
    }

    @Test
    fun `debug logging should call trainer with DEBUG level`() {
        Bark.d("Test debug message")

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals(Level.DEBUG, call.level)
        assertEquals("Test debug message", call.message)
    }

    @Test
    fun `info logging should call trainer with INFO level`() {
        Bark.i("Test info message")

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals(Level.INFO, call.level)
        assertEquals("Test info message", call.message)
    }

    @Test
    fun `warning logging should call trainer with WARNING level`() {
        Bark.w("Test warning message")

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals(Level.WARNING, call.level)
        assertEquals("Test warning message", call.message)
    }

    @Test
    fun `error logging should call trainer with ERROR level`() {
        Bark.e("Test error message")

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals(Level.ERROR, call.level)
        assertEquals("Test error message", call.message)
    }

    @Test
    fun `logging with throwable should pass throwable to trainer`() {
        val exception = RuntimeException("Test exception")
        Bark.e("Error occurred", exception)

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals(Level.ERROR, call.level)
        assertEquals("Error occurred", call.message)
        assertEquals(exception, call.throwable)
    }

    @Test
    fun `should be able to train multiple trainers`() {
        val trainer1 = TestTrainer()
        val trainer2 = TestTrainer()

        Bark.releaseAllTrainers()
        Bark.train(trainer1)
        Bark.train(trainer2)

        Bark.d("Test message")

        // Both trainers should receive the log call
        assertEquals(1, trainer1.getCallCount())
        assertEquals(1, trainer2.getCallCount())
        assertEquals("Test message", trainer1.getLastCall()?.message)
        assertEquals("Test message", trainer2.getLastCall()?.message)
    }

    @Test
    fun `releaseAllTrainers should clear all trainers`() {
        Bark.releaseAllTrainers()
        Bark.train(TestTrainer())
        Bark.train(TestTrainer())

        assertTrue(Bark.getStatus().contains("Trainers: 2"))

        Bark.releaseAllTrainers()

        assertTrue(Bark.getStatus().contains("Trainers: 0"))
    }

    @Test
    fun `muzzle should prevent all logging`() {
        Bark.muzzle()

        Bark.d("This should not be logged")
        Bark.e("Neither should this")

        assertEquals(0, testTrainer.getCallCount())
    }

    @Test
    fun `unmuzzle should re-enable logging`() {
        Bark.muzzle()
        Bark.d("Muzzled message")
        assertEquals(0, testTrainer.getCallCount())

        Bark.unmuzzle()
        Bark.d("Unmuzzled message")
        assertEquals(1, testTrainer.getCallCount())
        assertEquals("Unmuzzled message", testTrainer.getLastCall()?.message)
    }

    @Test
    fun `bark should start unmuzzled by default`() {
        Bark.releaseAllTrainers()
        Bark.train(testTrainer)

        Bark.d("Should work immediately")
        assertEquals(1, testTrainer.getCallCount())
    }

    @Test
    fun `global tag should override auto-detection`() {
        Bark.tag("CustomTag")
        Bark.d("Tagged message")

        val call = testTrainer.getLastCall()
        assertNotNull(call)
        assertEquals("CustomTag", call.tag)
    }

    @Test
    fun `changing global tag should affect subsequent logs`() {
        Bark.tag("FirstTag")
        Bark.d("First message")
        assertEquals("FirstTag", testTrainer.getLastCall()?.tag)

        Bark.tag("SecondTag")
        Bark.d("Second message")
        assertEquals("SecondTag", testTrainer.getLastCall()?.tag)
    }

    @Test
    fun `getStatus should report muzzled state`() {
        Bark.unmuzzle()
        assertTrue(Bark.getStatus().contains("Muzzled: false"))

        Bark.muzzle()
        assertTrue(Bark.getStatus().contains("Muzzled: true"))
    }

    @Test
    fun `getStatus should report global tag`() {
        Bark.tag("StatusTestTag")
        val status = Bark.getStatus()
        assertTrue(status.contains("[global] StatusTestTag"))
    }

    @Test
    fun `getStatus should report auto-detect when no global tag`() {
        // Clear global tag by creating a new instance (or implement a clearTag method)
        Bark.releaseAllTrainers()
        Bark.untag()
        val trainer = TestTrainer()
        Bark.train(trainer)

        val status = Bark.getStatus()
        assertTrue(status.contains("auto-detect"))
    }

    @Test
    fun `getStatus should report trainer count`() {
        Bark.releaseAllTrainers()
        assertTrue(Bark.getStatus().contains("Trainers: 0"))

        Bark.train(TestTrainer())
        assertTrue(Bark.getStatus().contains("Trainers: 1"))

        Bark.train(TestTrainer())
        assertTrue(Bark.getStatus().contains("Trainers: 2"))
    }

    @Test
    fun `getStatus should list trainer types`() {
        Bark.releaseAllTrainers()
        Bark.train(TestTrainer())

        val status = Bark.getStatus()
        assertTrue(status.contains("TestTrainer"))
    }
}