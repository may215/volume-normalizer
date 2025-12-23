package com.yourapp.volumenormalizer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * Utility for constructing the persistent notification required for a foreground
 * service. Displays service status and includes a STOP action so the user can
 * shut down the normalizer from the notification shade.
 */
object Notifications {
    private const val CHANNEL_ID = "vn_channel"
    private const val CHANNEL_NAME = "Volume Normalizer"

    /** Ensure that the notification channel is created on Android O+. */
    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW).apply {
                description = "Keeps the volume normalizer running"
            }
            nm.createNotificationChannel(ch)
        }
    }

    /** Build the running service notification with STOP button. */
    fun buildRunning(ctx: Context): Notification {
        ensureChannel(ctx)

        // Intent to stop the service when user taps STOP
        val stopIntent = Intent(ctx, NormalizerService::class.java).apply {
            action = NormalizerService.ACTION_STOP
        }
        val stopPi = PendingIntent.getService(
            ctx,
            1002,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .setContentTitle("Volume Normalizer active")
            .setContentText("Protecting you from loud spikes")
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, "STOP", stopPi)
            .build()
    }
}