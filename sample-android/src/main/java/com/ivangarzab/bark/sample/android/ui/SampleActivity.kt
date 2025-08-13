package com.ivangarzab.bark.sample.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.sample.android.BuildConfig
import com.ivangarzab.bark.sample.android.ui.other.NotificationService
import com.ivangarzab.bark.sample.android.ui.other.User
import com.ivangarzab.bark.sample.android.ui.other.UserRepository
import com.ivangarzab.bark.sample.android.ui.theme.BarKTheme
import com.ivangarzab.bark.trainers.AndroidLogTrainer
import com.ivangarzab.bark.trainers.ColoredUnitTestTrainer
import kotlinx.coroutines.launch

/**
 * Realistic sample app demonstrating barK logging in a typical Android app
 *
 * This shows barK being used naturally in:
 * - App initialization
 * - User interactions
 * - Background operations
 * - Error handling
 * - Different components (activities, services, repositories)
 */
class SampleActivity : ComponentActivity() {

    private val userRepository = UserRepository()
    private val notificationService = NotificationService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize barK - typical SDK setup
        initializeLogging()

        // App startup logging
        Bark.i("App launched")
        Bark.d("MainActivity created")

        setContent {
            BarKTheme {
                MainScreen(
                    userRepository = userRepository,
                    notificationService = notificationService
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Bark.d("MainActivity started")
    }

    override fun onResume() {
        super.onResume()
        Bark.v("MainActivity resumed")

        // Load initial data
        lifecycleScope.launch {
            try {
                userRepository.loadUsers()
            } catch (e: Exception) {
                Bark.e("Failed to load initial data", e)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Bark.v("MainActivity paused")
    }

    override fun onDestroy() {
        super.onDestroy()
        Bark.d("MainActivity destroyed")
    }

    private fun initializeLogging() {
        // Typical SDK logging setup
        Bark.train(AndroidLogTrainer(volume = Level.DEBUG))

        // Add test trainer for development
        if (BuildConfig.DEBUG) {
            Bark.train(ColoredUnitTestTrainer(volume = Level.INFO))
        }

        Bark.i("Logging initialized")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userRepository: UserRepository,
    notificationService: NotificationService
) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load users on startup
    LaunchedEffect(Unit) {
        Bark.d("Loading initial user data")
        users = userRepository.getUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("barK Sample App") },
                actions = {
                    IconButton(
                        onClick = {
                            Bark.d("Settings clicked")
                            val intent = Intent(context, DebugActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Debug")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        Bark.i("Adding new user")
                        isLoading = true
                        errorMessage = null

                        try {
                            val newUser = userRepository.createUser("User ${users.size + 1}")
                            users = users + newUser
                            notificationService.showSuccess("User created successfully")
                        } catch (e: Exception) {
                            Bark.e("Failed to create user", e)
                            errorMessage = "Failed to create user: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Add, contentDescription = "Add User")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Error message
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // User stats
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "User Statistics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Users: ${users.size}")
                    Text("Active Users: ${users.count { it.isActive }}")
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            Bark.i("Refreshing user data")
                            isLoading = true
                            try {
                                users = userRepository.loadUsers()
                                notificationService.showInfo("Data refreshed")
                            } catch (e: Exception) {
                                Bark.e("Failed to refresh data", e)
                                errorMessage = "Refresh failed: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refresh")
                }

                Button(
                    onClick = {
                        scope.launch {
                            Bark.w("Clearing all users")
                            users = emptyList()
                            userRepository.clearUsers()
                            notificationService.showWarning("All users cleared")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear")
                }
            }

            // Users list
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Users",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (users.isEmpty()) {
                        Text(
                            text = "No users yet. Tap + to add one!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(users) { user ->
                                UserItem(
                                    user = user,
                                    onToggleActive = {
                                        scope.launch {
                                            val updatedUser = userRepository.toggleUserActive(user.id)
                                            users = users.map { if (it.id == user.id) updatedUser else it }
                                        }
                                    },
                                    onDelete = {
                                        scope.launch {
                                            Bark.w("Deleting user: ${user.name}")
                                            userRepository.deleteUser(user.id)
                                            users = users.filter { it.id != user.id }
                                            notificationService.showInfo("User deleted")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (user.isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (user.isActive) "Active" else "Inactive",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onToggleActive) {
                    Icon(
                        if (user.isActive) Icons.Default.Check else Icons.Default.Clear,
                        contentDescription = if (user.isActive) "Deactivate" else "Activate"
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}