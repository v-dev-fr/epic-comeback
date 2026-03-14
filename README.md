# Back Recovery App

An offline-first Android application designed for long-term recovery from L4-L5 disc herniation, featuring IBS tracking, habit persistence, and smart gamification. Built with Kotlin, Jetpack Compose, Room, and WorkManager following Clean Architecture principles.

## Features Included

### 1. Robust Offline Architecture

- **Tech Stack**: Kotlin, Jetpack Compose, Room Database, Coroutines/Flow, WorkManager, AlarmManager.
- **Clean Architecture**: Separation of Presentation (UI), Domain (UseCases), and Data (Room DB & Repositories) layers.
- **Data Safety**: Configured for Google Drive Auto-Backup via `backup_rules.xml`. Room Database migrations are explicitly required (no destructive fallbacks).

### 2. Smart Alarm & Reminder System

- **Precision Timers**: Uses `AlarmManager` with `RTC_WAKEUP` to ensure notifications fire exactly on time, independent of Doze mode.
- **Boot Resilience**: Implements `BootCompletedReceiver` and `TimeSetReceiver` to restore all alarms natively if the device restarts.
- **Snooze & Actionable Alerts**: Features a `SnoozeBroadcastReceiver` extending alarms. Posture reminders ("20/5 Rule") include detailed actionable text ("Engage light core brace") instead of generic pings.
- **Rest Logic**: The 20/5 reminders intelligently pause when the daily `restDay` toggle is activated.

### 3. Gamified Dashboard & Exercise Tracker

- **Gamification**: Includes a Level & XP system tied to daily completion percentages, Streak tracking (with Flame/Star tags), and celebratory hooks for UI completion.
- **Exercises**: Uses Reverse Pyramid logic. Enforces _McKenzie Directional Preference Logic_ on Week 1 Day 3 (flagging flexion responses as contraindications).
- **Phase 4 (Maintenance)**: Explicitly defined schedule (McGill Big 3, Glute Bridge, Cat-Cow, Walks) allowing users to add custom exercises seamlessly.

### 4. Advanced Health Tracking & Exports

- **Dual-Axis Progress**: The Domain `IbsPainCorrelationUseCase` feeds unique charts correlating IBS Severity with Back Pain scores.
- **Exporting**: Features an `ExportScreen` generating a PDF report of weight, pain, and water trends via Android's native `PdfDocument` and saving it locally via the `MediaStore` API.

### 5. Onboarding & Security

- **Strict Validation**: Input bounds enforced (Height 100-250cm, Weight 30-300kg).
- **Permission Management**: Explicit requests for Exact Alarms and Battery Optimization exemption during onboarding to ensure the app functions robustly on OEM forks (e.g., Xiaomi, Samsung).

## Project Structure

```
app/src/main/java/com/recovery/back/
├── data/local/
│   ├── AppDatabase.kt          (Room DB + Migrations)
│   ├── dao/AppDao.kt           (Data Access Objects)
│   └── entity/Entities.kt      (Room Tables: Profile, Logs, Exercises, Alarms)
├── domain/
│   ├── repository/AppRepository.kt
│   └── usecase/                (PhaseAdvancement, BadDayProtocol, IbsPainCorrelation)
├── presentation/
│   ├── screens/                (Dashboard, Exercises, Log, Progress, Alarms, Onboarding)
│   └── ui/theme/               (Calm Blue/Green tokens + XpGold/StreakFlame)
├── receivers/
│   ├── AlarmReceiver.kt
│   ├── BootCompletedReceiver.kt
│   └── SnoozeBroadcastReceiver.kt
└── workers/
    └── DailyResetWorker.kt
```

## How to Build & Run

1. Open Android Studio.
2. Select **File > Open** and navigate to this repository directory `rcr.v2`.
3. Allow Gradle to sync dependencies (Room, Compose, WorkManager, Navigation).
4. Connect an Android device (API 26+) or emulator.
5. Click **Run 'app'**.

> **Note**: For PDF Export testing, ensure the device is running Android 10+ (API 29+) to utilize the `MediaStore` scoped storage implementation. For Alarms testing, trigger an alarm 1-minute in the future via settings, or run `adb shell am broadcast -a android.intent.action.BOOT_COMPLETED` to test the Boot Receiver.
