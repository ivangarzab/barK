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

        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: true))

        Bark.i("=== Setting up UserRepositoryTests ===")

        userRepository = UserRepository()

        Bark.d("UserRepository instance created")
    }

    func testLoadUsersShouldReturnInitialUserList() async {
        Bark.i("Testing UserRepository.loadUsers()")

        let users = await userRepository.loadUsers()

        Bark.d("Loaded \(users.count) users from repository")
        XCTAssertFalse(users.isEmpty, "Should load at least some users")

        // Verify user structure
        for user in users {
            Bark.v("User: \(user.name) (ID: \(user.id), Active: \(user.isActive))")
            XCTAssertFalse(user.id.isEmpty, "User ID should not be empty")
            XCTAssertFalse(user.name.isEmpty, "User name should not be empty")
        }

        Bark.i("✅ loadUsers() test completed successfully")
    }

    func testCreateUserShouldAddNewUserToRepository() async throws {
        Bark.i("Testing user creation")

        let initialUsers = userRepository.getUsers()
        let initialCount = initialUsers.count
        Bark.d("Initial user count: \(initialCount)")

        let newUser = try await userRepository.createUser(name: "Test User")

        Bark.i("Created user: \(newUser.name) with ID: \(newUser.id)")

        let updatedUsers = userRepository.getUsers()
        Bark.d("Updated user count: \(updatedUsers.count)")

        XCTAssertEqual(initialCount + 1, updatedUsers.count, "User count should increase by 1")
        XCTAssertTrue(updatedUsers.contains(where: { $0.id == newUser.id }), "New user should be in the list")

        Bark.i("✅ User creation test passed")
    }

    func testCreateUserWithEmptyNameShouldThrowException() async {
        Bark.i("Testing user creation with invalid input")

        do {
            _ = try await userRepository.createUser(name: "")
            XCTFail("Should have thrown exception for empty name")
        } catch {
            Bark.i("✅ Correctly caught error: \(error.localizedDescription)")
        }

        // Test with blank name too
        do {
            _ = try await userRepository.createUser(name: "   ")
            XCTFail("Should have thrown exception for blank name")
        } catch {
            Bark.i("✅ Correctly caught exception for blank name")
        }
    }

    func testToggleUserActiveShouldChangeUserStatus() async throws {
        Bark.i("Testing user status toggle")

        // First create a user
        let user = try await userRepository.createUser(name: "Toggle Test User")
        let originalStatus = user.isActive
        Bark.d("Original user status: active=\(originalStatus)")

        // Toggle the status
        let updatedUser = try await userRepository.toggleUserActive(userId: user.id)
        Bark.i("Toggled user status: active=\(updatedUser.isActive)")

        XCTAssertNotEqual(originalStatus, updatedUser.isActive, "Status should have changed")
        XCTAssertEqual(user.id, updatedUser.id, "User ID should remain the same")
        XCTAssertEqual(user.name, updatedUser.name, "User name should remain the same")

        Bark.i("✅ User toggle test completed")
    }

    func testDeleteUserShouldRemoveUserFromRepository() async throws {
        Bark.i("Testing user deletion")

        // Create a user to delete
        let userToDelete = try await userRepository.createUser(name: "Delete Me")
        Bark.d("Created user for deletion: \(userToDelete.name)")

        let beforeDeletion = userRepository.getUsers()
        let initialCount = beforeDeletion.count

        // Delete the user
        try await userRepository.deleteUser(userId: userToDelete.id)
        Bark.i("Deleted user with ID: \(userToDelete.id)")

        let afterDeletion = userRepository.getUsers()
        let finalCount = afterDeletion.count

        XCTAssertEqual(initialCount - 1, finalCount, "User count should decrease by 1")
        XCTAssertFalse(afterDeletion.contains(where: { $0.id == userToDelete.id }),
                      "Deleted user should not be in list")

        Bark.i("✅ User deletion test passed")
    }

    func testClearUsersShouldRemoveAllUsers() async {
        Bark.i("Testing clear all users")

        // Ensure we have some users
        _ = await userRepository.loadUsers()
        let beforeClear = userRepository.getUsers()
        Bark.d("Users before clear: \(beforeClear.count)")

        await userRepository.clearUsers()

        let afterClear = userRepository.getUsers()
        Bark.i("Users after clear: \(afterClear.count)")

        XCTAssertTrue(afterClear.isEmpty, "Repository should be empty after clear")

        Bark.i("✅ Clear users test completed")
    }

    func testDemonstrateErrorLoggingWithExceptions() async {
        Bark.i("Testing error scenarios and exception logging")

        // Test operations on non-existent user
        do {
            _ = try await userRepository.toggleUserActive(userId: "nonexistent-id")
            XCTFail("Should have thrown exception for non-existent user")
        } catch {
           Bark.e(message: "Expected error when toggling non-existent user", throwable: error)
            Bark.i("✅ Correctly handled non-existent user toggle")
        }

        do {
            try await userRepository.deleteUser(userId: "another-fake-id")
            XCTFail("Should have thrown exception for non-existent user")
        } catch {
           Bark.e(message: "Expected error when deleting non-existent user", throwable: error)
            Bark.i("✅ Correctly handled non-existent user deletion")
        }

        Bark.i("✅ Error scenario testing completed")
    }

    func testAsyncOperationsWithColoredOutput() async throws {
        Bark.i("=== Testing Async Operations with Colored Logging ===")

        // Reload repository
        _ = await userRepository.loadUsers()

        // Create multiple users rapidly
        Bark.d("Creating multiple users")
        let names = ["Async User 1", "Async User 2", "Async User 3"]

        for name in names {
            let user = try await userRepository.createUser(name: name)
            Bark.v("Created: \(user.name)")
        }

        // Toggle some users
        let users = userRepository.getUsers()
        Bark.d("Toggling user states")
        for user in users.prefix(2) {
            let toggled = try await userRepository.toggleUserActive(userId: user.id)
            Bark.v("Toggled: \(toggled.name) -> \(toggled.isActive ? "active" : "inactive")")
        }

        Bark.i("✅ Async operations test completed")
    }

    func testRepositoryStateManagement() async throws {
        Bark.i("=== Testing Repository State Management ===")

        // Clear and start fresh
        await userRepository.clearUsers()
        Bark.d("Repository cleared")

        // Add users
        _ = try await userRepository.createUser(name: "User A")
        _ = try await userRepository.createUser(name: "User B")
        _ = try await userRepository.createUser(name: "User C")

        let users = userRepository.getUsers()
        Bark.i("Repository contains \(users.count) users")

        // Verify all users are present
        XCTAssertEqual(3, users.count, "Should have exactly 3 users")

        // Log each user
        for user in users {
            Bark.v("  - \(user.name) (ID: \(user.id), Active: \(user.isActive))")
        }

        Bark.i("✅ State management test completed")
    }

    func testDemonstrateLoggingAtDifferentVolumes() async throws {
        Bark.i("=== Testing Different Volume Levels ===")

        // Test with VERBOSE (show everything)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.verbose, showTimestamp: false))

        Bark.i("--- Volume: VERBOSE ---")
        await userRepository.clearUsers()
        _ = try await userRepository.createUser(name: "Verbose Test")

        // Test with INFO (hide debug/verbose)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.info, showTimestamp: false))

        Bark.i("--- Volume: INFO (debug/verbose filtered) ---")
        _ = try await userRepository.createUser(name: "Info Test")

        // Test with WARNING (only warnings and errors)
        Bark.releaseAllTrainers()
        Bark.train(trainer: ColoredUnitTestTrainer(volume: Level.warning, showTimestamp: false))

        Bark.i("--- Volume: WARNING (only warnings/errors shown) ---")
        await userRepository.clearUsers()

        Bark.i("✅ Volume demonstration completed")
    }
}
