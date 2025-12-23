package com.yourapp.volumenormalizer.audio

import android.os.SystemClock
import kotlin.math.max

/**
 * Detects loudness spikes relative to a slowly adapting baseline. When the current
 * RMS exceeds the baseline by a ratio the detector reports a spike. Afterwards
 * it schedules periodic RESTORE_TICK events to allow gradual volume recovery.
 * Sensitivity and restore speed can be tuned via setSensitivity and setRestoreSpeed.
 */
class LoudnessDetector {
    enum class Event { NONE, SPIKE, RESTORE_TICK }

    private var baseline = 0.02
    private var lastSpikeAt: Long = 0L
    private var lastRestoreTick: Long = 0L

    // Configurable by user via sliders (1.5..3.5 typical)
    private var spikeRatio = 2.2f
    // Restore speed controls how often a restore tick occurs (ms)
    private var restoreIntervalMs = 650L

    // Baseline tracking constants
    private val minBaseline = 0.005
    private val baselineAlpha = 0.01
    private val spikeCooldownMs = 1500L
    private val restoreStartDelayMs = 900L
    private val restoreWindowMs = 4500L

    /** Adjust spike ratio: higher = more sensitive (detect more spikes). */
    fun setSensitivity(value: Float) {
        spikeRatio = value
    }

    /** Adjust how quickly volume restores: higher value yields shorter interval. */
    fun setRestoreSpeed(value: Float) {
        // Map slider range 0.3..1.5 to interval between 900ms..300ms
        val clamped = value.coerceIn(0.3f, 1.5f)
        restoreIntervalMs = (900 - ((clamped - 0.3f) / 1.2f) * 600).toLong()
    }

    /** Process an RMS level and return a detector event. */
    fun update(rms: Double): Event {
        val now = SystemClock.elapsedRealtime()
        val b = max(baseline, minBaseline)

        // Spike detection: loudness jumps relative to baseline and outside cooldown
        val isSpike = rms > b * spikeRatio && (now - lastSpikeAt) > spikeCooldownMs
        if (isSpike) {
            lastSpikeAt = now
            return Event.SPIKE
        }

        // Update the baseline after the initial delay post spike
        if (now - lastSpikeAt > restoreStartDelayMs) {
            baseline = (1 - baselineAlpha) * b + baselineAlpha * rms
        }

        // Generate restore ticks at intervals within the restore window
        val withinWindow = (now - lastSpikeAt) in restoreStartDelayMs..restoreWindowMs
        if (withinWindow && (now - lastRestoreTick) > restoreIntervalMs) {
            lastRestoreTick = now
            return Event.RESTORE_TICK
        }

        return Event.NONE
    }
}