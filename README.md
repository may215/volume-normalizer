# Volume Normalizer for Android

System-wide loudness normalization to protect users from sudden volume spikes
(e.g. loud YouTube ads).

## Features
- Global mode (all apps)
- No audio latency
- No root required
- Android 10+
- Foreground service with STOP action
- Jetpack Compose UI

## How it works
The app monitors playback loudness using Android's AudioPlaybackCapture API.
When a sudden spike is detected, it temporarily lowers the media volume and
smoothly restores it afterward.

## Build
Open in Android Studio and run, or use:
./gradlew assembleDebug
