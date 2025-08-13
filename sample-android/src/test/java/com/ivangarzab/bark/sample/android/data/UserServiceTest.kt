package com.ivangarzab.bark.sample.android.data

import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.ColoredUnitTestTrainer
import com.ivangarzab.bark.trainers.UnitTestTrainer
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Test class for UserService demonstrating barK's test logging capabilities
 *
 * This test showcases:
 * - Automatic test detection
 * - Clean console output during tests
 * - Colored vs plain test output
 * - Different log levels in test context
 */
class UserServiceTest {

    private lateinit var userService: UserService

    @Before
    fun setup() {
        // Clear any existing trainers
        Bark.releaseAllTrainers()

        // Configure barK for testing - this will show in console output!
        Bark.train(ColoredUnitTestTrainer(volume = Level.DEBUG))

        Bark.i("=== Setting up UserServiceTest ===")

        userService = UserService()

        Bark.d("UserService instance created for testing")
    }

    @Test
    fun `performAction should execute successfully`() {
        Bark.i("Testing UserService.performAction()")

        // This should trigger the UserService's internal logging
        userService.performAction()

        Bark.v("performAction() completed without exceptions")
        Bark.i("✅ UserService.performAction() test passed")
    }

    @Test
    fun `multiple calls should work consistently`() {
        Bark.i("Testing multiple UserService calls")

        repeat(3) { index ->
            Bark.d("Performing action #${index + 1}")
            userService.performAction()
        }

        Bark.i("✅ Multiple calls test completed successfully")
    }

    @Test
    fun `demonstrate different log levels in tests`() {
        Bark.i("Demonstrating various log levels during testing:")

        Bark.v("VERBOSE: Very detailed test information")
        Bark.d("DEBUG: Test debugging information")
        Bark.i("INFO: General test progress")
        Bark.w("WARNING: Something to watch out for in tests")

        // Simulate a recoverable test scenario
        try {
            userService.performAction()
            Bark.i("Action completed successfully in test")
        } catch (e: Exception) {
            Bark.e("Unexpected error in test", e)
            fail("UserService should not throw exceptions")
        }

        Bark.i("✅ Log level demonstration completed")
    }

    @Test
    fun `show plain vs colored output comparison`() {
        Bark.i("====== Testing with ColoredUnitTestTrainer ======")

        // Use colored trainer
        Bark.releaseAllTrainers()
        Bark.train(ColoredUnitTestTrainer(volume = Level.VERBOSE))

        Bark.v("Verbose message (should be gray)")
        Bark.d("Debug message (should be blue)")
        Bark.i("Info message (should be green)")
        Bark.w("Warning message (should be yellow)")
        Bark.e("Error message (should be red)")

        userService.performAction()

        Bark.i("====== Switching to plain UnitTestTrainer ======")

        // Switch to plain trainer
        Bark.releaseAllTrainers()
        Bark.train(UnitTestTrainer(volume = Level.VERBOSE))

        Bark.v("Verbose message (plain text)")
        Bark.d("Debug message (plain text)")
        Bark.i("Info message (plain text)")
        Bark.w("Warning message (plain text)")
        Bark.e("Error message (plain text)")

        userService.performAction()

        Bark.i("✅ Trainer comparison completed")
    }
}