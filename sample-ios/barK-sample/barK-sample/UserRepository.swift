//
//  UserRepository.swift
//  barK-sample
//

import Foundation
import shared

class UserRepository: ObservableObject {
    @Published private(set) var users: [User] = []

    func loadUsers() async -> [User] {
        Bark.d("Loading users from repository")

        // Simulate network delay
        try? await Task.sleep(nanoseconds: 1_000_000_000)

        let loadedUsers = [
            User(id: "1", name: "Scooby-Doo", isActive: true),
            User(id: "2", name: "Snoopy", isActive: false),
            User(id: "3", name: "Air Bud", isActive: false),
            User(id: "4", name: "Pluto", isActive: true)
        ]

        await MainActor.run {
            self.users = loadedUsers
        }

        Bark.i("Loaded \(users.count) users")
        return users
    }

    func getUsers() -> [User] {
        Bark.v("Getting cached users")
        return users
    }

    func createUser(name: String) async throws -> User {
        Bark.i("Creating new user: \(name)")

        // Simulate creation delay
        try? await Task.sleep(nanoseconds: 500_000_000)

        // Simulate validation
        if name.trimmingCharacters(in: .whitespaces).isEmpty {
            Bark.e("Cannot create user with empty name")
            throw NSError(domain: "UserRepository", code: 1, userInfo: [NSLocalizedDescriptionKey: "Name cannot be empty"])
        }

        let user = User(
            id: String(Date().timeIntervalSince1970),
            name: name,
            isActive: true
        )

        await MainActor.run {
            self.users.append(user)
        }

        Bark.d("User created with ID: \(user.id)")
        return user
    }

    func toggleUserActive(userId: String) async throws -> User {
        Bark.d("Toggling active status for user: \(userId)")

        guard let index = users.firstIndex(where: { $0.id == userId }) else {
            Bark.e("User not found: \(userId)")
            throw NSError(domain: "UserRepository", code: 2, userInfo: [NSLocalizedDescriptionKey: "User not found"])
        }

        var user = users[index]
        user.isActive.toggle()

        await MainActor.run {
            self.users[index] = user
        }

        Bark.i("User \(user.name) is now \(user.isActive ? "active" : "inactive")")
        return user
    }

    func deleteUser(userId: String) async throws {
        Bark.w("Deleting user: \(userId)")

        guard let index = users.firstIndex(where: { $0.id == userId }) else {
            Bark.e("Failed to delete user: \(userId) not found")
            throw NSError(domain: "UserRepository", code: 3, userInfo: [NSLocalizedDescriptionKey: "User not found"])
        }

        await MainActor.run {
            self.users.remove(at: index)
        }

        Bark.d("User deleted successfully")
    }

    func clearUsers() async {
        Bark.w("Clearing all users")
        let count = users.count

        await MainActor.run {
            self.users.removeAll()
        }

        Bark.i("Cleared \(count) users")
    }
}
