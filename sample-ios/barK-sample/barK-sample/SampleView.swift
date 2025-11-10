//
//  SampleView.swift
//  barK-sample
//

import SwiftUI
import shared

/**
 * Realistic sample app demonstrating barK logging in a typical iOS app
 *
 * This shows barK being used naturally in:
 * - App initialization
 * - User interactions
 * - Background operations
 * - Error handling
 * - Different components (views, services, repositories)
 */
struct SampleView: View {
    @StateObject private var userRepository = UserRepository()
    private let notificationService = NotificationService()

    @State private var users: [User] = []
    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 16) {
                    // Error message
                    if let error = errorMessage {
                        HStack {
                            Text(error)
                                .foregroundColor(.white)
                                .padding()
                            Spacer()
                        }
                        .background(Color.red)
                        .cornerRadius(8)
                    }

                    // User stats
                    VStack(alignment: .leading, spacing: 8) {
                        Text("User Statistics")
                            .font(.headline)
                        Text("Total Users: \(users.count)")
                        Text("Active Users: \(users.filter { $0.isActive }.count)")
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(8)

                    // Action buttons
                    HStack(spacing: 8) {
                        Button(action: {
                            Task {
                                Bark.i("Refreshing user data")
                                isLoading = true
                                errorMessage = nil
                                do {
                                    users = try await userRepository.loadUsers()
                                    notificationService.showInfo("Data refreshed")
                                } catch {
                                    Bark.e("Failed to refresh data", throwable: error)
                                    errorMessage = "Refresh failed: \(error.localizedDescription)"
                                }
                                isLoading = false
                            }
                        }) {
                            Label("Refresh", systemImage: "arrow.clockwise")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)

                        Button(action: {
                            Task {
                                Bark.w("Clearing all users")
                                await userRepository.clearUsers()
                                users = []
                                notificationService.showWarning("All users cleared")
                            }
                        }) {
                            Label("Clear", systemImage: "xmark")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(.red)
                    }

                    // Users list
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Users")
                            .font(.headline)

                        if users.isEmpty {
                            Text("No users yet. Tap + to add one!")
                                .foregroundColor(.secondary)
                                .padding(.vertical, 32)
                        } else {
                            ForEach(users) { user in
                                UserRow(
                                    user: user,
                                    onToggleActive: {
                                        Task {
                                            do {
                                                let updatedUser = try await userRepository.toggleUserActive(userId: user.id)
                                                if let index = users.firstIndex(where: { $0.id == user.id }) {
                                                    users[index] = updatedUser
                                                }
                                            } catch {
                                                Bark.e("Failed to toggle user", throwable: error)
                                            }
                                        }
                                    },
                                    onDelete: {
                                        Task {
                                            Bark.w("Deleting user: \(user.name)")
                                            do {
                                                try await userRepository.deleteUser(userId: user.id)
                                                users.removeAll { $0.id == user.id }
                                                notificationService.showInfo("User deleted")
                                            } catch {
                                                Bark.e("Failed to delete user", throwable: error)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(Color(UIColor.secondarySystemBackground))
                    .cornerRadius(8)
                }
                .padding()
            }
            .navigationTitle("barK Sample App")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        Task {
                            Bark.i("Adding new user")
                            isLoading = true
                            errorMessage = nil

                            do {
                                let newUser = try await userRepository.createUser(name: "User \(users.count + 1)")
                                users.append(newUser)
                                notificationService.showSuccess("User created successfully")
                            } catch {
                                Bark.e("Failed to create user", throwable: error)
                                errorMessage = "Failed to create user: \(error.localizedDescription)"
                            }
                            isLoading = false
                        }
                    }) {
                        if isLoading {
                            ProgressView()
                        } else {
                            Image(systemName: "plus")
                        }
                    }
                }
            }
            .onAppear {
                Bark.d("SampleView appeared")
                Task {
                    do {
                        users = try await userRepository.loadUsers()
                    } catch {
                        Bark.e("Failed to load initial data", throwable: error)
                    }
                }
            }
            .onDisappear {
                Bark.d("SampleView disappeared")
            }
        }
    }
}

struct UserRow: View {
    let user: User
    let onToggleActive: () -> Void
    let onDelete: () -> Void

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(user.name)
                    .font(.body)
                    .fontWeight(.medium)
                Text(user.isActive ? "Active" : "Inactive")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Spacer()

            Button(action: onToggleActive) {
                Image(systemName: user.isActive ? "checkmark.circle.fill" : "xmark.circle")
                    .foregroundColor(user.isActive ? .green : .gray)
            }

            Button(action: onDelete) {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
        }
        .padding()
        .background(user.isActive ? Color.blue.opacity(0.1) : Color(UIColor.tertiarySystemBackground))
        .cornerRadius(8)
    }
}

#Preview {
    SampleView()
}
