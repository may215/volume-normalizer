# 🎚️ Volume Normalizer for Android
System-wide loudness protection against sudden volume spikes (e.g. loud YouTube ads)

![Platform](https://img.shields.io/badge/platform-Android-green)
![Min SDK](https://img.shields.io/badge/minSDK-29-blue)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-purple)
![License](https://img.shields.io/badge/license-Apache--2.0-orange)
![Status](https://img.shields.io/badge/status-stable-success)

---

## 📌 Project Motivation

Modern mobile media consumption suffers from inconsistent audio levels:
videos, ads, and short clips are often mixed at very different loudness levels.
This results in sudden, uncomfortable volume spikes that force users to
constantly adjust their device volume.

Android does not provide global loudness normalization, and apps control
their own audio output. Users currently have no system-wide protection.

### 🎯 Goal

Provide a safe, transparent, open-source solution that:
- Protects users from sudden loud sounds
- Keeps perceived volume consistent
- Works globally across all apps
- Requires no root, no hacks, and no audio rerouting
- Fully complies with Android and Play Store policies

---

## 🧠 Design Philosophy

1. No audio re-routing (no latency, no lip-sync issues)
2. No modification of other apps
3. User always stays in control

---

## 🔄 How It Works

```
Any App Audio
     ↓
Android Playback Capture API
     ↓
Real-time Loudness Analysis (RMS)
     ↓
Spike Detected?
   → Yes: Lower system volume
   → No: Do nothing
     ↓
Smooth volume restoration
```

No audio is recorded, stored, or transmitted.

---

## 🧩 Architecture

| Component | Responsibility |
|---------|----------------|
| MainActivity | UI and permission handling |
| NormalizerService | Foreground service |
| AudioCaptureEngine | Playback capture |
| LoudnessDetector | Spike detection |
| VolumeController | Volume adjustments |
| Prefs | Persistent settings |
| Notifications | Foreground notification |

---

## 🎛️ Features

- Global loudness protection (all apps)
- YouTube and ad spike protection
- Zero audio latency
- User-adjustable sensitivity and restore speed
- Foreground notification with STOP action
- Battery-friendly processing
- Respects manual volume changes

---

## 🔐 Permissions Explained

| Permission | Reason |
|----------|--------|
| Foreground Service | Reliable background operation |
| Media Projection | Required to capture playback audio |
| Notifications | Foreground controls |

Android shows a "screen capture" dialog due to OS limitations.
No visual data is accessed.

---

## 📦 Requirements

- Android 10+ (API 29+)
- Device supports AudioPlaybackCapture

---

## 🛠️ Build & Run

```bash
git clone https://github.com/yourname/volume-normalizer-android.git
cd volume-normalizer-android
./gradlew assembleDebug
```

APK location:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚠️ Known Limitations

- Loudness detection is heuristic-based
- Cannot semantically detect ads
- Some apps may opt out of playback capture

---

## 🧭 Roadmap

- App whitelist / blacklist
- Bluetooth-specific tuning
- Per-app sensitivity
- Improved loudness metrics
- Play Store release

---

## 🤝 Contributing

Contributions are welcome!
See CONTRIBUTING.md for details.

---

## 📜 License

Apache License 2.0

---

## ❤️ Why Open Source?

Ear safety and user comfort should be transparent and trustworthy.
Open source enables review, improvement, and trust.
