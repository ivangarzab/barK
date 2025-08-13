package com.ivangarzab.bark.sample.android.ui.other

import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.ColoredUnitTestTrainer
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Test class for UserRepository demonstrating barK's async logging capabilities
 *
 * This test showcases:
 * - Logging in suspend functions
 * - Error scenarios and exception logging
 * - Repository pattern testing with barK
 */
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        Bark.releaseAllTrainers()
        Bark.train(ColoredUnitTestTrainer(volume = Level.VERBOSE, showTimestamp = true))

        Bark.i("=== Setting up UserRepositoryTest ===")

        userRepository = UserRepository()

        Bark.d("UserRepository instance created")
    }

    @Test
    fun `loadUsers should return initial user list`() = runTest {
        Bark.i("Testing UserRepository.loadUsers()")

        val users = userRepository.loadUsers()

        Bark.d("Loaded ${users.size} users from repository")
        assertTrue("Should load at least some users", users.isNotEmpty())

        // Verify user structure
        users.forEach { user ->
            Bark.v("User: ${user.name} (ID: ${user.id}, Active: ${user.isActive})")
            assertNotNull("User ID should not be null", user.id)
            assertNotNull("User name should not be null", user.name)
        }

        Bark.i("✅ loadUsers() test completed successfully")
    }

    @Test
    fun `createUser should add new user to repository`() = runTest {
        Bark.i("Testing user creation")

        val initialUsers = userRepository.getUsers()
        val initialCount = initialUsers.size
        Bark.d("Initial user count: $initialCount")

        val newUser = userRepository.createUser("Test User")

        Bark.i("Created user: ${newUser.name} with ID: ${newUser.id}")

        val updatedUsers = userRepository.getUsers()
        Bark.d("Updated user count: ${updatedUsers.size}")

        assertEquals("User count should increase by 1", initialCount + 1, updatedUsers.size)
        assertTrue("New user should be in the list", updatedUsers.any { it.id == newUser.id })

        Bark.i("✅ User creation test passed")
    }

    @Test
    fun `createUser with empty name should throw exception`() = runTest {
        Bark.i("Testing user creation with invalid input")

        try {
            userRepository.createUser("")
            fail("Should have thrown exception for empty name")
        } catch (e: IllegalArgumentException) {
            Bark.i("✅ Correctly caught IllegalArgumentException: ${e.message}")
        } catch (e: Exception) {
            Bark.e("Unexpected exception type", e)
            fail("Expected IllegalArgumentException but got ${e::class.simpleName}")
        }

        // Test with blank name too
        try {
            userRepository.createUser("   ")
            fail("Should have thrown exception for blank name")
        } catch (e: IllegalArgumentException) {
            Bark.i("✅ Correctly caught exception for blank name")
        }
    }

    @Test
    fun `toggleUserActive should change user status`() = runTest {
        Bark.i("Testing user status toggle")

        // First create a user
        val user = userRepository.createUser("Toggle Test User")
        val originalStatus = user.isActive
        Bark.d("Original user status: active=$originalStatus")

        // Toggle the status
        val updatedUser = userRepository.toggleUserActive(user.id)
        Bark.i("Toggled user status: active=${updatedUser.isActive}")

        assertNotEquals("Status should have changed", originalStatus, updatedUser.isActive)
        assertEquals("User ID should remain the same", user.id, updatedUser.id)
        assertEquals("User name should remain the same", user.name, updatedUser.name)

        Bark.i("✅ User toggle test completed")
    }

    @Test
    fun `deleteUser should remove user from repository`() = runTest {
        Bark.i("Testing user deletion")

        // Create a user to delete
        val userToDelete = userRepository.createUser("Delete Me")
        Bark.d("Created user for deletion: ${userToDelete.name}")

        val beforeDeletion = userRepository.getUsers()
        val initialCount = beforeDeletion.size

        // Delete the user
        userRepository.deleteUser(userToDelete.id)
        Bark.i("Deleted user with ID: ${userToDelete.id}")

        val afterDeletion = userRepository.getUsers()
        val finalCount = afterDeletion.size

        assertEquals("User count should decrease by 1", initialCount - 1, finalCount)
        assertFalse("Deleted user should not be in list",
            afterDeletion.any { it.id == userToDelete.id })

        Bark.i("✅ User deletion test passed")
    }

    @Test
    fun `clearUsers should remove all users`() = runTest {
        Bark.i("Testing clear all users")

        // Ensure we have some users
        userRepository.loadUsers()
        val beforeClear = userRepository.getUsers()
        Bark.d("Users before clear: ${beforeClear.size}")

        userRepository.clearUsers()

        val afterClear = userRepository.getUsers()
        Bark.i("Users after clear: ${afterClear.size}")

        assertTrue("Repository should be empty after clear", afterClear.isEmpty())

        Bark.i("✅ Clear users test completed")
    }

    @Test
    fun `demonstrate error logging with exceptions`() = runTest {
        Bark.i("Testing error scenarios and exception logging")

        // Test operations on non-existent user
        try {
            userRepository.toggleUserActive("nonexistent-id")
            fail("Should have thrown exception for non-existent user")
        } catch (e: IllegalArgumentException) {
            Bark.e("Expected error when toggling non-existent user", e)
            Bark.i("✅ Correctly handled non-existent user toggle")
        }

        try {
            userRepository.deleteUser("another-fake-id")
            fail("Should have thrown exception for non-existent user")
        } catch (e: IllegalArgumentException) {
            Bark.e("Expected error when deleting non-existent user", e)
            Bark.i("✅ Correctly handled non-existent user deletion")
        }

        Bark.i("✅ Error scenario testing completed")
    }
}