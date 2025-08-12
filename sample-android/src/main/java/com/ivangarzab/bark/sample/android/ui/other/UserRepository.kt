package com.ivangarzab.bark.sample.android.ui.other

import com.ivangarzab.bark.Bark
import kotlinx.coroutines.delay

class UserRepository {
    private var users = mutableListOf<User>()

    suspend fun loadUsers(): List<User> {
        Bark.d("Loading users from repository")

        // Simulate network delay
        delay(1000)

        // Simulate potential failure
        if (Math.random() < 0.1) {
            throw Exception("Network timeout")
        }

        users = mutableListOf(
            User("1", "Alice Johnson", true),
            User("2", "Bob Smith", false),
            User("3", "Carol Davis", true)
        )

        Bark.i("Loaded ${users.size} users")
        return users.toList()
    }

    fun getUsers(): List<User> {
        Bark.v("Getting cached users")
        return users.toList()
    }

    suspend fun createUser(name: String): User {
        Bark.i("Creating new user: $name")

        // Simulate creation delay
        delay(500)

        // Simulate validation
        if (name.isBlank()) {
            Bark.e("Cannot create user with empty name")
            throw IllegalArgumentException("Name cannot be empty")
        }

        val user = User(
            id = System.currentTimeMillis().toString(),
            name = name,
            isActive = true
        )

        users.add(user)
        Bark.d("User created with ID: ${user.id}")

        return user
    }

    suspend fun toggleUserActive(userId: String): User {
        Bark.d("Toggling active status for user: $userId")

        val userIndex = users.indexOfFirst { it.id == userId }
        if (userIndex == -1) {
            Bark.e("User not found: $userId")
            throw IllegalArgumentException("User not found")
        }

        val user = users[userIndex]
        val updatedUser = user.copy(isActive = !user.isActive)
        users[userIndex] = updatedUser

        Bark.i("User ${updatedUser.name} is now ${if (updatedUser.isActive) "active" else "inactive"}")

        return updatedUser
    }

    suspend fun deleteUser(userId: String) {
        Bark.w("Deleting user: $userId")

        val removed = users.removeIf { it.id == userId }
        if (!removed) {
            Bark.e("Failed to delete user: $userId not found")
            throw IllegalArgumentException("User not found")
        }

        Bark.d("User deleted successfully")
    }

    suspend fun clearUsers() {
        Bark.w("Clearing all users")
        val count = users.size
        users.clear()
        Bark.i("Cleared $count users")
    }
}