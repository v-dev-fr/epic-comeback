package com.recovery.back.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class IbsSeverity { NONE, MILD, MODERATE, SEVERE }
enum class McKenziePreference { EXTENSION, FLEXION, UNCLEAR, NOT_TESTED }

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Single user
    val name: String,
    val heightCm: Int,
    val startWeightKg: Float,
    val goalWeightKg: Float,
    val injuryDateEpochDay: Long,
    val ibsSeverity: IbsSeverity,
    val mckenziePreference: McKenziePreference = McKenziePreference.NOT_TESTED,
    val currentPhase: Int = 1,
    val xp: Int = 0,
    val level: Int = 1
)

@Entity(tableName = "daily_log")
data class DailyLogEntity(
    @PrimaryKey val dateEpochDay: Long,
    val backPainScore: Int, // 0-10
    val sciaticPainScore: Int, // 0-10
    val ibsSeverityScore: Int, // 0-3 mapped
    val bloating: Boolean,
    val energyLevel: Int, // 1-5
    val notes: String,
    val completionPercentage: Float = 0f,
    val restDay: Boolean = false
)

@Entity(tableName = "weight_log")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateEpochDay: Long,
    val weightKg: Float
)

@Entity(tableName = "exercise_session")
data class ExerciseSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateEpochDay: Long,
    val exerciseId: String,
    val repsCompleted: Int,
    val painAfter: Int, // 0-10
    val notes: String,
    val weekNumber: Int,
    val phaseNumber: Int
)

@Entity(tableName = "meal_log")
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateEpochDay: Long,
    val mealName: String,
    val calories: Int
)

@Entity(tableName = "water_log")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateEpochDay: Long,
    val glassCount: Int
)

@Entity(tableName = "supplement_log")
data class SupplementLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateEpochDay: Long,
    val supplementName: String,
    val taken: Boolean
)

@Entity(tableName = "alarm_config")
data class AlarmConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val baseTimeMillisOfDay: Long, // Time of day for the alarm
    val repeatIntervalMillis: Long, // 0 if not repeating
    val enabled: Boolean,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean,
    val nextTriggerTime: Long, // Epoch ms for BootCompleted Receiver
    val isWorkdayOnly: Boolean = false // For 20/5 move reminder
)

@Entity(tableName = "pain_threshold_event")
data class PainThresholdEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateEpochDay: Long,
    val painScore: Int,
    val triggerType: String, // "bad_day", "phase_regression", "contraindication"
    val actionTaken: String
)

@Entity(tableName = "custom_exercise")
data class CustomExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sets: Int,
    val reps: Int,
    val isActiveInPhase4: Boolean = true
)
