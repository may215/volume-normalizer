package com.yourapp.volumenormalizer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.yourapp.volumenormalizer.ui.theme.VolumeNormalizerTheme

/**
 * Main entry point for the app. Presents a simple UI to start/stop the normalizer
 * service and tweak sensitivity and restore speed. UI uses Jetpack Compose for
 * a modern look and feel similar to volume booster apps.
 */
class MainActivity : ComponentActivity() {

    // Acquire the MediaProjectionManager which is required to request audio capture
    private val projectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    // Launcher to start the permission activity for screen/audio capture
    private val captureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK && res.data != null) {
                // Start the foreground service with the result code and data from the projection activity
                val startIntent = Intent(this, NormalizerService::class.java).apply {
                    action = NormalizerService.ACTION_START
                    putExtra(NormalizerService.EXTRA_RESULT_CODE, res.resultCode)
                    putExtra(NormalizerService.EXTRA_DATA_INTENT, res.data)
                }
                ContextCompat.startForegroundService(this, startIntent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VolumeNormalizerTheme {
                // Compose UI for controlling the normalizer
                VolumeNormalizerScreen(
                    onStart = {
                        // Request permission to capture audio output
                        val intent = projectionManager.createScreenCaptureIntent()
                        captureLauncher.launch(intent)
                    },
                    onStop = {
                        // Stop the foreground service
                        startService(Intent(this, NormalizerService::class.java).apply {
                            action = NormalizerService.ACTION_STOP
                        })
                    }
                )
            }
        }
    }
}

/**
 * Composable representing the main screen of the app. Displays the status (active/idle),
 * provides start/stop button and exposes sliders to adjust sensitivity and restore speed.
 * Sliders persist their values using Prefs so settings are retained across launches.
 */
@Composable
fun VolumeNormalizerScreen(
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    var running by remember { mutableStateOf(false) }

    val ctx = androidx.compose.ui.platform.LocalContext.current
    // Persistent settings loaded from shared prefs
    var sensitivity by remember { mutableStateOf(Prefs.getSensitivity(ctx)) }
    var restore by remember { mutableStateOf(Prefs.getRestoreSpeed(ctx)) }

    // Save settings when they change
    fun saveSettings() {
        Prefs.setSensitivity(ctx, sensitivity)
        Prefs.setRestoreSpeed(ctx, restore)
    }

    // Background gradient reminiscent of volume booster apps
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B0B12), Color(0xFF1F1F5F), Color(0xFF0B0B12))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column {
            Text(
                text = "Volume Normalizer",
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Global mode • All apps",
                style = MaterialTheme.typography.body2,
                color = Color(0xFFB0B0B0)
            )
        }

        // Status indicator in a circular container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color(0x33111111), CircleShape)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFF1F1F5F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (running) "ACTIVE" else "IDLE",
                        color = Color.White,
                        style = MaterialTheme.typography.h5
                    )
                }
            }
        }

        // Sensitivity slider and label
        Column {
            Text("Sensitivity", color = Color.White)
            Slider(
                value = sensitivity,
                onValueChange = {
                    sensitivity = it
                    saveSettings()
                },
                valueRange = 1.5f..3.5f
            )
            Text(
                text = "Higher = reacts more often",
                style = MaterialTheme.typography.caption,
                color = Color(0xFFB0B0B0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Restore speed slider
            Text("Restore Speed", color = Color.White)
            Slider(
                value = restore,
                onValueChange = {
                    restore = it
                    saveSettings()
                },
                valueRange = 0.3f..1.5f
            )
            Text(
                text = "Higher = returns faster",
                style = MaterialTheme.typography.caption,
                color = Color(0xFFB0B0B0)
            )
        }

        // Start/Stop button
        Button(
            onClick = {
                running = !running
                if (running) onStart() else onStop()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF4081))
        ) {
            Text(
                text = if (running) "STOP" else "START",
                color = Color.White
            )
        }
    }
}