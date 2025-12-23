package com.yourapp.volumenormalizer.audio

import android.media.AudioManager
import android.os.SystemClock
import kotlin.math.abs

/**
 * Controls the system media stream volume during loudness normalization. Responds
 * to spike events by quickly lowering volume and then gradually restores it.
 * Also tracks user manual changes so as not to fight their desired target.
 */
class VolumeController(private val audioManager: AudioManager) {
    private val stream = AudioManager.STREAM_MUSIC

    // The normal volume level the user expects
    private var userTarget: Int = audioManager.getStreamVolume(stream)

    // The last volume set by the app to detect user adjustments
    private var lastSetByApp: Int = userTarget

    // Window during which the app is actively attenuating
    private var attenuationActiveUntil: Long = 0L

    /** Observe the current volume and update target if user changed volume. */
    fun observeUserTarget() {
        val current = audioManager.getStreamVolume(stream)
        val now = SystemClock.elapsedRealtime()
        // Only update target if user changed volume outside of attenuation window
        val appAttenuating = now < attenuationActiveUntil
        if (!appAttenuating && abs(current - lastSetByApp) >= 1) {
            userTarget = current
            lastSetByApp = current
        }
    }

    /** Lower the volume quickly in response to a spike. */
    fun fastDown() {
        val now = SystemClock.elapsedRealtime()
        // Set attenuation window a few seconds into the future
        attenuationActiveUntil = now + 4500L
        // Capture the current target
        userTarget = audioManager.getStreamVolume(stream)
        // Drop two steps, but don't go below volume 1 to avoid complete silence
        repeat(2) {
            val cur = audioManager.getStreamVolume(stream)
            if (cur > 1) {
                audioManager.adjustStreamVolume(stream, AudioManager.ADJUST_LOWER, 0)
                lastSetByApp = audioManager.getStreamVolume(stream)
            }
        }
    }

    /** Raise the volume slowly back towards the user's target. */
    fun slowRestoreTick() {
        val current = audioManager.getStreamVolume(stream)
        if (current < userTarget) {
            audioManager.adjustStreamVolume(stream, AudioManager.ADJUST_RAISE, 0)
            lastSetByApp = audioManager.getStreamVolume(stream)
        }
    }
}