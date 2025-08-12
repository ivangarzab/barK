package com.ivangarzab.bark.sample.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.sample.android.ui.other.UserRepository
import com.ivangarzab.bark.sample.android.ui.other.UserService
import com.ivangarzab.bark.sample.android.ui.theme.BarKTheme
import com.ivangarzab.bark.trainers.AndroidLogTrainer
import com.ivangarzab.bark.trainers.ColoredTestTrainer
import com.ivangarzab.bark.trainers.TestTrainer

/**
 * Debug activity for testing barK features
 *
 * This is the "testing harness" activity that lets developers
 * experiment with all barK features manually.
 */
class DebugActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Bark.i("Debug activity opened")

        setContent {
            BarKTheme {
                DebugScreen(
                    onBack = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Bark.d("Debug activity closed")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(onBack: () -> Unit) {
    var status by remember { mutableStateOf(Bark.getStatus()) }

    fun updateStatus() {
        status = Bark.getStatus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("barK Debug Console") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Debug & Test barK Features",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Use these controls to test barK functionality. Watch output in Logcat and IDE console.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Log Level Testing
            LogLevelSection { updateStatus() }

            // Trainer Management
            TrainerSection { updateStatus() }

            // Muzzle Controls
            MuzzleSection { updateStatus() }

            // Tag Controls
            TagSection { updateStatus() }

            // Multi-class Demo
            DemoSection { updateStatus() }

            // Status Display
            StatusSection(status = status)
        }
    }
}

@Composable
private fun LogLevelSection(onStatusUpdate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Log Levels",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Bark.v("VERBOSE: Most detailed logging level")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("VERBOSE", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        Bark.d("DEBUG: Development and troubleshooting info")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("DEBUG", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        Bark.i("INFO: General application flow information")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("INFO", fontSize = 10.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Bark.w("WARNING: Something unexpected happened")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("WARNING", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        val exception = RuntimeException("This is a test exception")
                        Bark.e("ERROR: Something went wrong!", exception)
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("ERROR", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun TrainerSection(onStatusUpdate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Trainer Management",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Switch between different log outputs:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Bark.releaseAllTrainers()
                        Bark.train(AndroidLogTrainer(volume = Level.DEBUG))
                        Bark.i("Switched to AndroidLogTrainer - check Logcat!")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Android Log", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        Bark.releaseAllTrainers()
                        Bark.train(TestTrainer(volume = Level.DEBUG))
                        Bark.i("Switched to TestTrainer - check IDE console!")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Console", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        Bark.releaseAllTrainers()
                        Bark.train(ColoredTestTrainer(volume = Level.DEBUG))
                        Bark.i("Switched to ColoredTestTrainer - check IDE console!")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Colored Test", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun MuzzleSection(onStatusUpdate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Muzzle Controls",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Control whether barK produces any output:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Bark.muzzle()
                        Bark.d("This message should NOT appear anywhere!")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Muzzle barK", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        Bark.unmuzzle()
                        Bark.d("barK is unmuzzled - this message should appear!")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Unmuzzle barK", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun TagSection(onStatusUpdate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tag Controls",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Control log tag behavior:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Bark.tag("DEBUG_CONSOLE")
                        Bark.i("Set global tag to 'DEBUG_CONSOLE'")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Set Global Tag", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        Bark.untag()
                        Bark.i("Cleared global tag - back to auto-detection")
                        onStatusUpdate()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Auto-Detect Tags", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun DemoSection(onStatusUpdate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Advanced Demos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    demonstrateAutoTagDetection()
                    onStatusUpdate()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Auto-Tag Detection")
            }

            Button(
                onClick = {
                    demonstrateExceptionLogging()
                    onStatusUpdate()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Exception Logging")
            }
        }
    }
}

@Composable
private fun StatusSection(status: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "barK Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = status,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

// Demo functions
private fun demonstrateAutoTagDetection() {
    Bark.i("=== Auto-Tag Detection Demo ===")

    // From this function
    Bark.d("This should show 'DebugActivity' as tag")

    // From helper method
    tagDetectionHelper()

    // From sample classes
    val service = UserService()
    service.performAction()

    val repo = UserRepository()
    // This is a suspend function, so we'll simulate it
    Bark.d("Would call repo.loadUsers() - tag should be 'UserRepository'")
}

private fun tagDetectionHelper() {
    Bark.d("Called from helper - should still show 'DebugActivity'")
    nestedTagTest()
}

private fun nestedTagTest() {
    Bark.d("Nested call - should still show 'DebugActivity'")
}

private fun demonstrateExceptionLogging() {
    Bark.i("=== Exception Logging Demo ===")

    try {
        throw IllegalStateException("This is a test exception")
    } catch (e: IllegalStateException) {
        Bark.e("Caught IllegalStateException", e)
    }

    try {
        throw RuntimeException("Another test exception",
            IllegalArgumentException("Nested cause"))
    } catch (e: RuntimeException) {
        Bark.e("Caught RuntimeException with nested cause", e)
    }
}