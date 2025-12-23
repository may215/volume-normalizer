package com.yourapp.volumenormalizer

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.yourapp.volumenormalizer.audio.AudioCaptureEngine

/**
 * Foreground service that runs the audio capture and volume adjustment engine. The
 * service can be started with ACTION_START and must receive the MediaProjection
 * result code and data intent extras so that the engine can capture audio. When
 * ACTION_STOP is received the service stops capturing and shuts down.
 */
class NormalizerService : Service() {

    companion object {
        const val ACTION_START = "vn.START"
        const val ACTION_STOP = "vn.STOP"
        const val EXTRA_RESULT_CODE = "vn.RESULT_CODE"
        const val EXTRA_DATA_INTENT = "vn.DATA_INTENT"
        private const val NOTIFICATION_ID = 1001

        @Volatile
        var isRunning = false
            private set
    }

    private var engine: AudioCaptureEngine? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                // Put the service in the foreground with persistent notification
                startForeground(NOTIFICATION_ID, Notifications.buildRunning(this))
                // Extract projection result and data
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
                val data = intent.getParcelableExtra<Intent>(EXTRA_DATA_INTENT)
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Start or restart the engine with the new projection
                    engine?.stop()
                    engine = AudioCaptureEngine(this).apply { start(resultCode, data) }
                    isRunning = true
                }
            }
            ACTION_STOP -> {
                // Shut down engine and stop the service
                engine?.stop()
                engine = null
                isRunning = false
                // Remove the foreground notification and stop the service
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        // Clean up the engine if the service is destroyed unexpectedly
        engine?.stop()
        engine = null
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}