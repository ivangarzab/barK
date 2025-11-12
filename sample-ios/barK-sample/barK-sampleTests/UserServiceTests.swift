//
//  UserServiceTests.swift
//  barK-sampleTests
//

import XCTest
import shared
@testable import barK_sample

/**
 * Test class for UserService demonstrating barK's test logging capabilities
 *
 * This test showcases:
 * - Automatic test detection
 * - Clean console output during tests
 * - Colored vs plain test output
 * - Different log levels in test context
 */
final class UserServiceTests: XCTestCase {

    private var userService: UserService!

    override func setUp() {
        super.setUp()

        // Clear any existing trainers
        Bark.releaseAllTrainers()

        // Configure barK for testing - this will show in console output!
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.debug, showTimestamp: false))

        Bark.i("=== Setting up UserServiceTests ===")

        userService = UserService()

        Bark.d("UserService instance created for testing")
    }

    func testPerformActionShouldExecuteSuccessfully() {
        Bark.i("Testing UserService.performAction()")

        // This should trigger the UserService's internal logging
        userService.performAction()

        Bark.v("performAction() completed without exceptions")
        Bark.i("✅ UserService.performAction() test passed")
    }

    func testMultipleCallsShouldWorkConsistently() {
        Bark.i("Testing multiple UserService calls")

        for index in 0..<3 {
            Bark.d("Performing action #\(index + 1)")
            userService.performAction()
        }

        Bark.i("✅ Multiple calls test completed successfully")
    }

    func testDemonstrateDifferentLogLevelsInTests() {
        Bark.i("Demonstrating various log levels during testing:")

        Bark.v("VERBOSE: Very detailed test information")
        Bark.d("DEBUG: Test debugging information")
        Bark.i("INFO: General test progress")
        Bark.w("WARNING: Something to watch out for in tests")

        // Simulate a successful test scenario
        do {
            userService.performAction()
            Bark.i("Action completed successfully in test")
        } catch { 
           Bark.e(message: "Unexpected error in test", throwable: error)
            XCTFail("UserService should not throw exceptions")
        }

        Bark.i("✅ Log level demonstration completed")
    }

    func testShowPlainVsColoredOutputComparison() {
        Bark.i("====== Testing with ColoredUnitTestTrainer ======")

        // Use colored trainer
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.v("Verbose message (should be gray)")
        Bark.d("Debug message (should be blue)")
        Bark.i("Info message (should be green)")
        Bark.w("Warning message (should be yellow)")
        Bark.e("Error message (should be red)")

        userService.performAction()

        Bark.i("====== Switching to plain UnitTestTrainer ======")

        // Switch to plain trainer
        Bark.releaseAllTrainers()
        Bark.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.v("Verbose message (plain text)")
        Bark.d("Debug message (plain text)")
        Bark.i("Info message (plain text)")
        Bark.w("Warning message (plain text)")
        Bark.e("Error message (plain text)")

        userService.performAction()

        Bark.i("✅ Trainer comparison completed")
    }

    func testDemonstrateTimestampOptions() {
        Bark.i("=== Testing Timestamp Options ===")

        // Test with timestamps enabled
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.i("--- With Timestamps ---")
        userService.performAction()
        Bark.d("Notice the HH:mm:ss.SSS timestamps on each line")

        // Test with timestamps disabled
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.i("--- Without Timestamps ---")
        userService.performAction()
        Bark.d("Notice the cleaner output without timestamps")

        Bark.i("✅ Timestamp options demonstration completed")
    }

    func testVolumeFilteringInAction() {
        Bark.i("=== Testing Volume Filtering ===")

        // Test VERBOSE level (shows everything)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.i("--- Volume: VERBOSE (shows all) ---")
        Bark.v("Verbose message")
        Bark.d("Debug message")
        Bark.i("Info message")
        Bark.w("Warning message")

        // Test WARNING level (shows warnings and above)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.warning, showTimestamp: false))

        Bark.i("--- Volume: WARNING (filters verbose, debug, info) ---")
        Bark.v("Verbose message (filtered)")
        Bark.d("Debug message (filtered)")
        Bark.i("Info message (filtered)")
        Bark.w("Warning message (visible)")
        Bark.e("Error message (visible)")

        Bark.i("✅ Volume filtering demonstration completed")
    }
}
