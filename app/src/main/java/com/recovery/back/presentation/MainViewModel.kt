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
    }
    val todayMeals: StateFlow<List<MealLogEntity>> = appDao.getMealsByDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<AlarmConfigEntity>> = appDao.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val alarmScheduler = com.recovery.back.util.AlarmScheduler(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = appDao.getEnabledAlarmsSync()
            if (existing.isEmpty()) {
                val alarmsToInsert = listOf(
                    AlarmConfigEntity(label = "Posture Check (20/5)", baseTimeMillisOfDay = 0, repeatIntervalMillis = 1200000, enabled = true, soundEnabled = true, vibrationEnabled = true, nextTriggerTime = 0, isWorkdayOnly = true),
                    AlarmConfigEntity(label = "Water Reminder", baseTimeMillisOfDay = 0, repeatIntervalMillis = 3600000, enabled = true, soundEnabled = false, vibrationEnabled = true, nextTriggerTime = 0),
                    AlarmConfigEntity(label = "Evening Routine", baseTimeMillisOfDay = 64800000, repeatIntervalMillis = 0, enabled = true, soundEnabled = true, vibrationEnabled = true, nextTriggerTime = 0)
                )
                alarmsToInsert.forEach { alarm ->
                    val id = appDao.insertAlarm(alarm)
                    alarmScheduler.scheduleAlarm(alarm.copy(id = id.toInt()))
                }
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
            val updatedAlarm = alarm.copy(enabled = enabled)
            appDao.updateAlarm(updatedAlarm)
            if (enabled) {
                alarmScheduler.scheduleAlarm(updatedAlarm)
            } else {
                alarmScheduler.cancelAlarm(updatedAlarm.id)
            }
        }
    }

    val weightLogs: StateFlow<List<WeightLogEntity>> = appDao.getWeightLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val painConsistencyCorrelation: StateFlow<List<Pair<Float, Float>>> = appDao.getAllDailyLogs()
        .map { logs ->
            logs.take(30).map { log ->
                val avgPain = (log.backPainScore + log.sciaticPainScore) / 2f
                Pair(avgPain, log.completionPercentage)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consistencyScore: StateFlow<Float> = appDao.getAllDailyLogs()
        .map { logs ->
            if (logs.isEmpty()) 0f
            else logs.take(30).map { it.completionPercentage }.average().toFloat()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val workoutStreak: StateFlow<Int> = appDao.getAllDailyLogs()
        .map { logs ->
            var streak = 0
            var currentDate = LocalDate.now()
            
            // Check today
            val todayLog = logs.find { it.dateEpochDay == currentDate.toEpochDay() }
            if (todayLog != null && todayLog.completionPercentage >= 0.8f) {
                streak++
                currentDate = currentDate.minusDays(1)
                
                // Check backwards
                while (true) {
                    val log = logs.find { it.dateEpochDay == currentDate.toEpochDay() }
                    if (log != null && log.completionPercentage >= 0.8f) {
                        streak++
                        currentDate = currentDate.minusDays(1)
                    } else {
                        break
                    }
                }
            } else {
                // If today is not done, check if yesterday was done (to maintain streak)
                currentDate = currentDate.minusDays(1)
                while (true) {
                    val log = logs.find { it.dateEpochDay == currentDate.toEpochDay() }
                    if (log != null && log.completionPercentage >= 0.8f) {
                        streak++
                        currentDate = currentDate.minusDays(1)
                    } else {
                        break
                    }
                }
            }
            streak
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addWater() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = waterLog.value
            if (current == null) {
                appDao.insertWaterLog(WaterLogEntity(dateEpochDay = today, glassCount = 1))
            } else {
                appDao.insertWaterLog(current.copy(glassCount = current.glassCount + 1))
            }
            // Award XP for hydration
            val profile = userProfile.value
            if (profile != null) {
                appDao.insertUserProfile(profile.copy(xp = profile.xp + 10))
            }
        }
    }

    fun logExerciseCompletion(exerciseId: String, reps: Int, phase: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appDao.insertExerciseSession(
                ExerciseSessionEntity(
                    dateEpochDay = today,
                    exerciseId = exerciseId,
                    repsCompleted = reps,
                    painAfter = 0,
                    notes = "",
                    weekNumber = 1,
                    phaseNumber = phase
                )
            )
            
            // Re-calculate completion percentage for the day
            // (Simplified: let's say 4 exercises = 100%)
            val sessions = appDao.getExerciseSessionsSync(today)
            val percentage = (sessions.size.toFloat() / 4f).coerceAtMost(1f)
            
            val currentLog = dailyLog.value ?: DailyLogEntity(
                dateEpochDay = today,
                backPainScore = 0,
                sciaticPainScore = 0,
                ibsSeverityScore = 0,
                bloating = false,
                energyLevel = 3,
                notes = ""
            )
            appDao.insertDailyLog(currentLog.copy(completionPercentage = percentage))

            // Award XP
            val profile = userProfile.value
            if (profile != null) {
                appDao.insertUserProfile(profile.copy(xp = profile.xp + 50))
            }
        }
    }
}
