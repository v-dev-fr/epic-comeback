package com.recovery.back.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.recovery.back.data.local.AppDatabase
import com.recovery.back.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val appDao = db.appDao()

    val userProfile: StateFlow<UserProfileEntity?> = appDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val today = LocalDate.now().toEpochDay()

    val waterLog: StateFlow<WaterLogEntity?> = appDao.getWaterLog(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyLog: StateFlow<DailyLogEntity?> = appDao.getDailyLog(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addWater() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = waterLog.value
            if (current == null) {
                appDao.insertWaterLog(WaterLogEntity(dateEpochDay = today, glassCount = 1))
            } else {
                appDao.insertWaterLog(current.copy(glassCount = current.glassCount + 1))
            }
        }
    }

    fun logWeight(weight: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            appDao.insertWeightLog(WeightLogEntity(dateEpochDay = today, weightKg = weight))
        }
    }

    fun logPain(backPain: Int, sciaticPain: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = dailyLog.value ?: DailyLogEntity(
                dateEpochDay = today,
                backPainScore = backPain,
                sciaticPainScore = sciaticPain,
                ibsSeverityScore = 0,
                bloating = false,
                energyLevel = 3,
                notes = ""
            )
            appDao.insertDailyLog(current.copy(backPainScore = backPain, sciaticPainScore = sciaticPain))
            
            if (backPain >= 7 || sciaticPain >= 7) {
                appDao.insertPainEvent(PainThresholdEventEntity(
                    dateEpochDay = today,
                    painScore = maxOf(backPain, sciaticPain),
                    triggerType = "bad_day",
                    actionTaken = "Manual log spike"
                ))
            }
        }
    }

    fun toggleRestDay(isRest: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = dailyLog.value ?: DailyLogEntity(
                dateEpochDay = today,
                backPainScore = 0,
                sciaticPainScore = 0,
                ibsSeverityScore = 0,
                bloating = false,
                energyLevel = 3,
                notes = ""
            )
            appDao.insertDailyLog(current.copy(restDay = isRest))
        }
    val todayMeals: StateFlow<List<MealLogEntity>> = appDao.getMealsByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<AlarmConfigEntity>> = appDao.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = appDao.getEnabledAlarmsSync()
            if (existing.isEmpty()) {
                appDao.insertAlarm(AlarmConfigEntity(label = "Posture Check (20/5)", baseTimeMillisOfDay = 0, repeatIntervalMillis = 1200000, enabled = true, soundEnabled = true, vibrationEnabled = true, nextTriggerTime = 0, isWorkdayOnly = true))
                appDao.insertAlarm(AlarmConfigEntity(label = "Water Reminder", baseTimeMillisOfDay = 0, repeatIntervalMillis = 3600000, enabled = true, soundEnabled = false, vibrationEnabled = true, nextTriggerTime = 0))
                appDao.insertAlarm(AlarmConfigEntity(label = "Evening Routine", baseTimeMillisOfDay = 64800000, repeatIntervalMillis = 0, enabled = true, soundEnabled = true, vibrationEnabled = true, nextTriggerTime = 0))
            }
        }
    }

    fun logMeal(name: String, calories: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appDao.insertMealLog(
                MealLogEntity(
                    dateEpochDay = today,
                    mealName = name,
                    calories = calories
                )
            )
        }
    }

    fun updateAlarm(alarm: AlarmConfigEntity, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            appDao.updateAlarm(alarm.copy(enabled = enabled))
        }
    }

    fun logExerciseCompletion(exerciseId: String, reps: Int, phase: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appDao.insertExerciseSession(
                ExerciseSessionEntity(
                    dateEpochDay = today,
                    exerciseId = exerciseId,
                    repsCompleted = reps,
                    painAfter = 0, // Default, could be asked in a dialog
                    notes = "",
                    weekNumber = 1, // Simplified
                    phaseNumber = phase
                )
            )
            // Logic to increase XP
            val profile = userProfile.value
            if (profile != null) {
                appDao.insertUserProfile(profile.copy(xp = profile.xp + 50))
            }
        }
    }
}
