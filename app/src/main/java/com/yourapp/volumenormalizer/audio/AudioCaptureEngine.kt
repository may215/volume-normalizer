package com.yourapp.volumenormalizer.audio

import android.content.Context
import android.content.Intent
import android.media.*
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.SystemClock
import com.yourapp.volumenormalizer.Prefs

/**
 * Engine that captures mixed playback audio via MediaProjection and uses a
 * LoudnessDetector to find sudden spikes. When a spike is detected it
 * immediately lowers the system media volume and then gradually restores it
 * using the VolumeController. Settings from Prefs control sensitivity and
 * restore speed and are periodically refreshed.
 */
class AudioCaptureEngine(private val ctx: Context) {
    private val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val volumeController = VolumeController(audioManager)
    private val detector = LoudnessDetector()

    private var projection: MediaProjection? = null
    private var recorder: AudioRecord? = null
    private var captureThread: Thread? = null
    @Volatile private var running = false

    /** Start capturing audio with the given media projection result. */
    fun start(resultCode: Int, data: Intent) {
        // Obtain the MediaProjection instance
        val pm = ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projection = pm.getMediaProjection(resultCode, data)

        // Configure the AudioPlaybackCapture API to match media and game usage
        val captureConfig = AudioPlaybackCaptureConfiguration.Builder(projection!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .addMatchingUsage(AudioAttributes.USAGE_GAME)
            .build()

        // Define an audio format. We'll capture 48kHz stereo PCM 16-bit
        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(48000)
            .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
            .build()

        // Determine a safe buffer size (at least 1 second of audio)
        val minBuffer = AudioRecord.getMinBufferSize(48000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        val bufferSize = minBuffer.coerceAtLeast(48000)

        // Create the AudioRecord that will receive the captured audio
        recorder = AudioRecord.Builder()
            .setAudioPlaybackCaptureConfig(captureConfig)
            .setAudioFormat(format)
            .setBufferSizeInBytes(bufferSize)
            .build()

        // Start reading from the AudioRecord on a background thread
        running = true
        recorder!!.startRecording()
        captureThread = Thread {
            val buffer = ShortArray(2048)
            var lastPrefsRefresh = 0L
            while (running) {
                // Read audio into buffer
                val read = recorder!!.read(buffer, 0, buffer.size)
                if (read <= 0) continue

                // Refresh settings from preferences occasionally
                val now = SystemClock.elapsedRealtime()
                if (now - lastPrefsRefresh > 500) {
                    detector.setSensitivity(Prefs.getSensitivity(ctx))
                    detector.setRestoreSpeed(Prefs.getRestoreSpeed(ctx))
                    lastPrefsRefresh = now
                }

                // Compute RMS amplitude and update the detector
                val rms = computeRms(buffer, read)
                val event = detector.update(rms)

                // Respect user manual volume changes
                volumeController.observeUserTarget()

                // React to detector events
                when (event) {
                    LoudnessDetector.Event.SPIKE -> volumeController.fastDown()
                    LoudnessDetector.Event.RESTORE_TICK -> volumeController.slowRestoreTick()
                    else -> {}
                }
            }
        }.also { it.start() }
    }

    /** Stop the capture and release resources. */
    fun stop() {
        running = false
        try { captureThread?.join(600) } catch (_: Exception) {}
        captureThread = null
        // Stop and release the AudioRecord
        recorder?.run {
            try { stop() } catch (_: Exception) {}
            release()
        }
        recorder = null
        // Stop the projection
        projection?.stop()
        projection = null
    }

    /** Compute the root mean square of the audio buffer to get a normalized level (0..1). */
    private fun computeRms(buf: ShortArray, n: Int): Double {
        var sum = 0.0
        for (i in 0 until n) {
            val v = buf[i].toDouble()
            sum += v * v
        }
        val mean = sum / n
        return kotlin.math.sqrt(mean) / 32768.0
    }
}