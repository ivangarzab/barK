//
//  UserRepositoryTests.swift
//  barK-sampleTests
//

import XCTest
import shared
@testable import barK_sample

/**
 * Test class for UserRepository demonstrating barK's async logging capabilities
 *
 * This test showcases:
 * - Logging in async functions
 * - Error scenarios and exception logging
 * - Repository pattern testing with barK
 */
final class UserRepositoryTests: XCTestCase {

    private var userRepository: UserRepository!

    override func setUp() {
        super.setUp()

        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.shared.i(message: "=== Setting up UserRepositoryTests ===", throwable: nil)

        userRepository = UserRepository()

        Bark.shared.d(message: "UserRepository instance created", throwable: nil)
    }

    func testLoadUsersShouldReturnInitialUserList() async {
        Bark.shared.i(message: "Testing UserRepository.loadUsers()", throwable: nil)

        let users = await userRepository.loadUsers()

        Bark.shared.d(message: "Loaded \(users.count) users from repository", throwable: nil)
        XCTAssertFalse(users.isEmpty, "Should load at least some users")

        // Verify user structure
        for user in users {
            Bark.shared.v(message: "User: \(user.name) (ID: \(user.id), Active: \(user.isActive))", throwable: nil)
            XCTAssertFalse(user.id.isEmpty, "User ID should not be empty")
            XCTAssertFalse(user.name.isEmpty, "User name should not be empty")
        }

        Bark.shared.i(message: "✅ loadUsers() test completed successfully", throwable: nil)
    }

    func testCreateUserShouldAddNewUserToRepository() async throws {
        Bark.shared.i(message: "Testing user creation", throwable: nil)

        let initialUsers = userRepository.getUsers()
        let initialCount = initialUsers.count
        Bark.shared.d(message: "Initial user count: \(initialCount)", throwable: nil)

        let newUser = try await userRepository.createUser(name: "Test User")

        Bark.shared.i(message: "Created user: \(newUser.name) with ID: \(newUser.id)", throwable: nil)

        let updatedUsers = userRepository.getUsers()
        Bark.shared.d(message: "Updated user count: \(updatedUsers.count)", throwable: nil)

        XCTAssertEqual(initialCount + 1, updatedUsers.count, "User count should increase by 1")
        XCTAssertTrue(updatedUsers.contains(where: { $0.id == newUser.id }), "New user should be in the list")

        Bark.shared.i(message: "✅ User creation test passed", throwable: nil)
    }

    func testCreateUserWithEmptyNameShouldThrowException() async {
        Bark.shared.i(message: "Testing user creation with invalid input", throwable: nil)

        do {
            _ = try await userRepository.createUser(name: "")
            XCTFail("Should have thrown exception for empty name")
        } catch {
            Bark.shared.i(message: "✅ Correctly caught error: \(error.localizedDescription)", throwable: nil)
        }

        // Test with blank name too
        do {
            _ = try await userRepository.createUser(name: "   ")
            XCTFail("Should have thrown exception for blank name")
        } catch {
            Bark.shared.i(message: "✅ Correctly caught exception for blank name", throwable: nil)
        }
    }

    func testToggleUserActiveShouldChangeUserStatus() async throws {
        Bark.shared.i(message: "Testing user status toggle", throwable: nil)

        // First create a user
        let user = try await userRepository.createUser(name: "Toggle Test User")
        let originalStatus = user.isActive
        Bark.shared.d(message: "Original user status: active=\(originalStatus)", throwable: nil)

        // Toggle the status
        let updatedUser = try await userRepository.toggleUserActive(userId: user.id)
        Bark.shared.i(message: "Toggled user status: active=\(updatedUser.isActive)", throwable: nil)

        XCTAssertNotEqual(originalStatus, updatedUser.isActive, "Status should have changed")
        XCTAssertEqual(user.id, updatedUser.id, "User ID should remain the same")
        XCTAssertEqual(user.name, updatedUser.name, "User name should remain the same")

        Bark.shared.i(message: "✅ User toggle test completed", throwable: nil)
    }

    func testDeleteUserShouldRemoveUserFromRepository() async throws {
        Bark.shared.i(message: "Testing user deletion", throwable: nil)

        // Create a user to delete
        let userToDelete = try await userRepository.createUser(name: "Delete Me")
        Bark.shared.d(message: "Created user for deletion: \(userToDelete.name)", throwable: nil)

        let beforeDeletion = userRepository.getUsers()
        let initialCount = beforeDeletion.count

        // Delete the user
        try await userRepository.deleteUser(userId: userToDelete.id)
        Bark.shared.i(message: "Deleted user with ID: \(userToDelete.id)", throwable: nil)

        let afterDeletion = userRepository.getUsers()
        let finalCount = afterDeletion.count

        XCTAssertEqual(initialCount - 1, finalCount, "User count should decrease by 1")
        XCTAssertFalse(afterDeletion.contains(where: { $0.id == userToDelete.id }),
                      "Deleted user should not be in list")

        Bark.shared.i(message: "✅ User deletion test passed", throwable: nil)
    }

    func testClearUsersShouldRemoveAllUsers() async {
        Bark.shared.i(message: "Testing clear all users", throwable: nil)

        // Ensure we have some users
        _ = await userRepository.loadUsers()
        let beforeClear = userRepository.getUsers()
        Bark.shared.d(message: "Users before clear: \(beforeClear.count)", throwable: nil)

        await userRepository.clearUsers()

        let afterClear = userRepository.getUsers()
        Bark.shared.i(message: "Users after clear: \(afterClear.count)", throwable: nil)

        XCTAssertTrue(afterClear.isEmpty, "Repository should be empty after clear")

        Bark.shared.i(message: "✅ Clear users test completed", throwable: nil)
    }

    func testDemonstrateErrorLoggingWithExceptions() async {
        Bark.shared.i(message: "Testing error scenarios and exception logging", throwable: nil)

        // Test operations on non-existent user
        do {
            _ = try await userRepository.toggleUserActive(userId: "nonexistent-id")
            XCTFail("Should have thrown exception for non-existent user")
        } catch {
//            Bark.shared.e(message: "Expected error when toggling non-existent user", throwable: error as NSError)
            Bark.shared.i(message: "✅ Correctly handled non-existent user toggle", throwable: nil)
        }

        do {
            try await userRepository.deleteUser(userId: "another-fake-id")
            XCTFail("Should have thrown exception for non-existent user")
        } catch {
//            Bark.shared.e(message: "Expected error when deleting non-existent user", throwable: error as NSError)
            Bark.shared.i(message: "✅ Correctly handled non-existent user deletion", throwable: nil)
        }

        Bark.shared.i(message: "✅ Error scenario testing completed", throwable: nil)
    }

    func testAsyncOperationsWithColoredOutput() async throws {
        Bark.shared.i(message: "=== Testing Async Operations with Colored Logging ===", throwable: nil)

        // Reload repository
        _ = await userRepository.loadUsers()

        // Create multiple users rapidly
        Bark.shared.d(message: "Creating multiple users", throwable: nil)
        let names = ["Async User 1", "Async User 2", "Async User 3"]

        for name in names {
            let user = try await userRepository.createUser(name: name)
            Bark.shared.v(message: "Created: \(user.name)", throwable: nil)
        }

        // Toggle some users
        let users = userRepository.getUsers()
        Bark.shared.d(message: "Toggling user states", throwable: nil)
        for user in users.prefix(2) {
            let toggled = try await userRepository.toggleUserActive(userId: user.id)
            Bark.shared.v(message: "Toggled: \(toggled.name) -> \(toggled.isActive ? "active" : "inactive")", throwable: nil)
        }

        Bark.shared.i(message: "✅ Async operations test completed", throwable: nil)
    }

    func testRepositoryStateManagement() async throws {
        Bark.shared.i(message: "=== Testing Repository State Management ===", throwable: nil)

        // Clear and start fresh
        await userRepository.clearUsers()
        Bark.shared.d(message: "Repository cleared", throwable: nil)

        // Add users
        _ = try await userRepository.createUser(name: "User A")
        _ = try await userRepository.createUser(name: "User B")
        _ = try await userRepository.createUser(name: "User C")

        let users = userRepository.getUsers()
        Bark.shared.i(message: "Repository contains \(users.count) users", throwable: nil)

        // Verify all users are present
        XCTAssertEqual(3, users.count, "Should have exactly 3 users")

        // Log each user
        for user in users {
            Bark.shared.v(message: "  - \(user.name) (ID: \(user.id), Active: \(user.isActive))", throwable: nil)
        }

        Bark.shared.i(message: "✅ State management test completed", throwable: nil)
    }

    func testDemonstrateLoggingAtDifferentVolumes() async throws {
        Bark.shared.i(message: "=== Testing Different Volume Levels ===", throwable: nil)

        // Test with VERBOSE (show everything)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.shared.i(message: "--- Volume: VERBOSE ---", throwable: nil)
        await userRepository.clearUsers()
        _ = try await userRepository.createUser(name: "Verbose Test")

        // Test with INFO (hide debug/verbose)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.info, showTimestamp: false))

        Bark.shared.i(message: "--- Volume: INFO (debug/verbose filtered) ---", throwable: nil)
        _ = try await userRepository.createUser(name: "Info Test")

        // Test with WARNING (only warnings and errors)
        Bark.shared.releaseAllTrainers()
        Bark.shared.train(trainer: ColoredUnitTestTrainer(volume: Level.warning, showTimestamp: false))

        Bark.shared.i(message: "--- Volume: WARNING (only warnings/errors shown) ---", throwable: nil)
        await userRepository.clearUsers()

        Bark.shared.i(message: "✅ Volume demonstration completed", throwable: nil)
    }
}
