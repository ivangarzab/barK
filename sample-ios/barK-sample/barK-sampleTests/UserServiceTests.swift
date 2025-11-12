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
        Bark.shared.releaseAllTrainers()

        // Configure barK for testing - this will show in console output!
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.debug, showTimestamp: false))

        Bark.shared.i(message: "=== Setting up UserServiceTests ===", throwable: nil)

        userService = UserService()

        Bark.shared.d(message: "UserService instance created for testing", throwable: nil)
    }

    func testPerformActionShouldExecuteSuccessfully() {
        Bark.shared.i(message: "Testing UserService.performAction()", throwable: nil)

        // This should trigger the UserService's internal logging
        userService.performAction()

        Bark.shared.v(message: "performAction() completed without exceptions", throwable: nil)
        Bark.shared.i(message: "✅ UserService.performAction() test passed", throwable: nil)
    }

    func testMultipleCallsShouldWorkConsistently() {
        Bark.shared.i(message: "Testing multiple UserService calls", throwable: nil)

        for index in 0..<3 {
            Bark.shared.d(message: "Performing action #\(index + 1)", throwable: nil)
            userService.performAction()
        }

        Bark.shared.i(message: "✅ Multiple calls test completed successfully", throwable: nil)
    }

    func testDemonstrateDifferentLogLevelsInTests() {
        Bark.shared.i(message: "Demonstrating various log levels during testing:", throwable: nil)

        Bark.shared.v(message: "VERBOSE: Very detailed test information", throwable: nil)
        Bark.shared.d(message: "DEBUG: Test debugging information", throwable: nil)
        Bark.shared.i(message: "INFO: General test progress", throwable: nil)
        Bark.shared.w(message: "WARNING: Something to watch out for in tests", throwable: nil)

        // Simulate a successful test scenario
        do {
            userService.performAction()
            Bark.shared.i(message: "Action completed successfully in test", throwable: nil)
        } catch {
//            Bark.shared.e(message: "Unexpected error in test", throwable: error as NSError)
            XCTFail("UserService should not throw exceptions")
        }

        Bark.shared.i(message: "✅ Log level demonstration completed", throwable: nil)
    }

    func testShowPlainVsColoredOutputComparison() {
        Bark.shared.i(message: "====== Testing with ColoredUnitTestTrainer ======", throwable: nil)

        // Use colored trainer
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.v(message: "Verbose message (should be gray)", throwable: nil)
        Bark.shared.d(message: "Debug message (should be blue)", throwable: nil)
        Bark.shared.i(message: "Info message (should be green)", throwable: nil)
        Bark.shared.w(message: "Warning message (should be yellow)", throwable: nil)
        Bark.shared.e(message: "Error message (should be red)", throwable: nil)

        userService.performAction()

        Bark.shared.i(message: "====== Switching to plain UnitTestTrainer ======", throwable: nil)

        // Switch to plain trainer
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: UnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.v(message: "Verbose message (plain text)", throwable: nil)
        Bark.shared.d(message: "Debug message (plain text)", throwable: nil)
        Bark.shared.i(message: "Info message (plain text)", throwable: nil)
        Bark.shared.w(message: "Warning message (plain text)", throwable: nil)
        Bark.shared.e(message: "Error message (plain text)", throwable: nil)

        userService.performAction()

        Bark.shared.i(message: "✅ Trainer comparison completed", throwable: nil)
    }

    func testDemonstrateTimestampOptions() {
        Bark.shared.i(message: "=== Testing Timestamp Options ===", throwable: nil)

        // Test with timestamps enabled
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.shared.i(message: "--- With Timestamps ---", throwable: nil)
        userService.performAction()
        Bark.shared.d(message: "Notice the HH:mm:ss.SSS timestamps on each line", throwable: nil)

        // Test with timestamps disabled
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.i(message: "--- Without Timestamps ---", throwable: nil)
        userService.performAction()
        Bark.shared.d(message: "Notice the cleaner output without timestamps", throwable: nil)

        Bark.shared.i(message: "✅ Timestamp options demonstration completed", throwable: nil)
    }

    func testVolumeFilteringInAction() {
        Bark.shared.i(message: "=== Testing Volume Filtering ===", throwable: nil)

        // Test VERBOSE level (shows everything)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.i(message: "--- Volume: VERBOSE (shows all) ---", throwable: nil)
        Bark.shared.v(message: "Verbose message", throwable: nil)
        Bark.shared.d(message: "Debug message", throwable: nil)
        Bark.shared.i(message: "Info message", throwable: nil)
        Bark.shared.w(message: "Warning message", throwable: nil)

        // Test WARNING level (shows warnings and above)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.warning, showTimestamp: false))

        Bark.shared.i(message: "--- Volume: WARNING (filters verbose, debug, info) ---", throwable: nil)
        Bark.shared.v(message: "Verbose message (filtered)", throwable: nil)
        Bark.shared.d(message: "Debug message (filtered)", throwable: nil)
        Bark.shared.i(message: "Info message (filtered)", throwable: nil)
        Bark.shared.w(message: "Warning message (visible)", throwable: nil)
        Bark.shared.e(message: "Error message (visible)", throwable: nil)

        Bark.shared.i(message: "✅ Volume filtering demonstration completed", throwable: nil)
    }
}
